package io.spring.application;

import static java.util.stream.Collectors.toList;

import io.spring.application.data.ArticleData;
import io.spring.application.data.ArticleDataList;
import io.spring.application.data.ArticleFavoriteCount;
import io.spring.core.user.User;
import io.spring.infrastructure.mybatis.readservice.ArticleFavoritesReadService;
import io.spring.infrastructure.mybatis.readservice.ArticleReadService;
import io.spring.infrastructure.mybatis.readservice.UserRelationshipQueryService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.springframework.web.util.HtmlUtils;

@Service
@AllArgsConstructor
public class ArticleQueryService {
  private ArticleReadService articleReadService;
  private UserRelationshipQueryService userRelationshipQueryService;
  private ArticleFavoritesReadService articleFavoritesReadService;

  public Optional<ArticleData> findById(String id, User user) {
    ArticleData articleData = articleReadService.findById(id);
    if (articleData == null) {
      return Optional.empty();
    } else {
      if (user != null) {
        fillExtraInfo(id, user, articleData);
      }
      return Optional.of(articleData);
    }
  }

  public Optional<ArticleData> findBySlug(String slug, User user) {
    ArticleData articleData = articleReadService.findBySlug(slug);
    if (articleData == null) {
      return Optional.empty();
    } else {
      if (user != null) {
        fillExtraInfo(articleData.getId(), user, articleData);
      }
      return Optional.of(articleData);
    }
  }

  public CursorPager<ArticleData> findRecentArticlesWithCursor(
      String tag,
      String author,
      String favoritedBy,
      CursorPageParameter<DateTime> page,
      User currentUser) {
    List<String> articleIds =
        articleReadService.findArticlesWithCursor(tag, author, favoritedBy, page);
    if (articleIds.size() == 0) {
      return new CursorPager<>(new ArrayList<>(), page.getDirection(), false);
    } else {
      boolean hasExtra = articleIds.size() > page.getLimit();
      if (hasExtra) {
        articleIds.remove(page.getLimit());
      }
      if (!page.isNext()) {
        Collections.reverse(articleIds);
      }

      List<ArticleData> articles = articleReadService.findArticles(articleIds);
      fillExtraInfo(articles, currentUser);

      return new CursorPager<>(articles, page.getDirection(), hasExtra);
    }
  }

  public CursorPager<ArticleData> findUserFeedWithCursor(
      User user, CursorPageParameter<DateTime> page) {
    List<String> followdUsers = userRelationshipQueryService.followedUsers(user.getId());
    if (followdUsers.size() == 0) {
      return new CursorPager<>(new ArrayList<>(), page.getDirection(), false);
    } else {
      List<ArticleData> articles =
          articleReadService.findArticlesOfAuthorsWithCursor(followdUsers, page);
      boolean hasExtra = articles.size() > page.getLimit();
      if (hasExtra) {
        articles.remove(page.getLimit());
      }
      if (!page.isNext()) {
        Collections.reverse(articles);
      }
      fillExtraInfo(articles, user);
      return new CursorPager<>(articles, page.getDirection(), hasExtra);
    }
  }

  public ArticleDataList findRecentArticles(
      String tag, String author, String favoritedBy, Page page, User currentUser) {
    List<String> articleIds = articleReadService.queryArticles(tag, author, favoritedBy, page);
    int articleCount = articleReadService.countArticle(tag, author, favoritedBy);
    if (articleIds.size() == 0) {
      return new ArticleDataList(new ArrayList<>(), articleCount);
    } else {
      List<ArticleData> articles = articleReadService.findArticles(articleIds);
      fillExtraInfo(articles, currentUser);
      return new ArticleDataList(articles, articleCount);
    }
  }

  public ArticleDataList findUserFeed(User user, Page page) {
    List<String> followdUsers = userRelationshipQueryService.followedUsers(user.getId());
    if (followdUsers.size() == 0) {
      return new ArticleDataList(new ArrayList<>(), 0);
    } else {
      List<ArticleData> articles = articleReadService.findArticlesOfAuthors(followdUsers, page);
      fillExtraInfo(articles, user);
      int count = articleReadService.countFeedSize(followdUsers);
      return new ArticleDataList(articles, count);
    }
  }

  private void fillExtraInfo(List<ArticleData> articles, User currentUser) {
    setFavoriteCount(articles);
    if (currentUser != null) {
      setIsFavorite(articles, currentUser);
      setIsFollowingAuthor(articles, currentUser);
    }
  }

  private void setIsFollowingAuthor(List<ArticleData> articles, User currentUser) {
    Set<String> followingAuthors =
        userRelationshipQueryService.followingAuthors(
            currentUser.getId(),
            articles.stream()
                .map(articleData1 -> articleData1.getProfileData().getId())
                .collect(toList()));
    articles.forEach(
        articleData -> {
          if (followingAuthors.contains(articleData.getProfileData().getId())) {
            articleData.getProfileData().setFollowing(true);
          }
        });
  }

  private void setFavoriteCount(List<ArticleData> articles) {
    List<ArticleFavoriteCount> favoritesCounts =
        articleFavoritesReadService.articlesFavoriteCount(
            articles.stream().map(ArticleData::getId).collect(toList()));
    Map<String, Integer> countMap = new HashMap<>();
    favoritesCounts.forEach(
        item -> {
          countMap.put(item.getId(), item.getCount());
        });
    articles.forEach(
        articleData -> articleData.setFavoritesCount(countMap.get(articleData.getId())));
  }

  private void setIsFavorite(List<ArticleData> articles, User currentUser) {
    Set<String> favoritedArticles =
        articleFavoritesReadService.userFavorites(
            articles.stream().map(articleData -> articleData.getId()).collect(toList()),
            currentUser);

    articles.forEach(
        articleData -> {
          if (favoritedArticles.contains(articleData.getId())) {
            articleData.setFavorited(true);
          }
        });
  }

  private void fillExtraInfo(String id, User user, ArticleData articleData) {
    articleData.setFavorited(articleFavoritesReadService.isUserFavorite(user.getId(), id));
    articleData.setFavoritesCount(articleFavoritesReadService.articleFavoriteCount(id));
    articleData
        .getProfileData()
        .setFollowing(
            userRelationshipQueryService.isUserFollowing(
                user.getId(), articleData.getProfileData().getId()));
  }

  // jira-dev-pipeline:block:start existing-service-ArticleQueryService
// @generated-by jira-dev-pipeline
  // @generated-ticket SCRUM-14

  public byte[] exportArticlesExcel(String tag, String author, String favoritedBy, User user) {
    List<ArticleData> exportArticles = new ArrayList<>();
    int offset = 0;
    while (true) {
      ArticleDataList articleDataList =
          findRecentArticles(tag, author, favoritedBy, new Page(offset, 100), user);
      List<ArticleData> pageArticles =
          articleDataList == null || articleDataList.getArticleDatas() == null
              ? List.of()
              : articleDataList.getArticleDatas();
      exportArticles.addAll(pageArticles);
      if (pageArticles.size() < 100) {
        break;
      }
      offset += 100;
    }
    List<List<String>> rows = new ArrayList<>();
    for (ArticleData articleData : exportArticles) {
      rows.add(
          List.of(
              nullToEmpty(articleData.getSlug()),
              nullToEmpty(articleData.getTitle()),
              nullToEmpty(articleData.getDescription()),
              articleData.getProfileData() == null
                  ? ""
                  : nullToEmpty(articleData.getProfileData().getUsername()),
              articleData.getCreatedAt() == null ? "" : articleData.getCreatedAt().toString()));
    }
    return buildGeneratedWorkbook(
        "articles",
        List.of("slug", "title", "description", "author", "createdAt"),
        rows);
  }


  private byte[] buildGeneratedWorkbook(
      String sheetName, List<String> headers, List<List<String>> rows) {
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zipOutputStream =
            new ZipOutputStream(outputStream, StandardCharsets.UTF_8)) {
      writeZipEntry(
          zipOutputStream,
          "[Content_Types].xml",
          "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
              + "<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\">"
              + "<Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/>"
              + "<Default Extension=\"xml\" ContentType=\"application/xml\"/>"
              + "<Override PartName=\"/xl/workbook.xml\""
              + " ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml\"/>"
              + "<Override PartName=\"/xl/worksheets/sheet1.xml\""
              + " ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml\"/>"
              + "<Override PartName=\"/xl/styles.xml\""
              + " ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.styles+xml\"/>"
              + "</Types>");
      writeZipEntry(
          zipOutputStream,
          "_rels/.rels",
          "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
              + "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">"
              + "<Relationship Id=\"rId1\""
              + " Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument\""
              + " Target=\"xl/workbook.xml\"/>"
              + "</Relationships>");
      writeZipEntry(
          zipOutputStream,
          "xl/workbook.xml",
          "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
              + "<workbook xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\""
              + " xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\">"
              + "<sheets><sheet name=\"" + HtmlUtils.htmlEscape(sheetName) + "\" sheetId=\"1\" r:id=\"rId1\"/></sheets>"
              + "</workbook>");
      writeZipEntry(
          zipOutputStream,
          "xl/_rels/workbook.xml.rels",
          "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
              + "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">"
              + "<Relationship Id=\"rId1\""
              + " Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\""
              + " Target=\"worksheets/sheet1.xml\"/>"
              + "<Relationship Id=\"rId2\""
              + " Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles\""
              + " Target=\"styles.xml\"/>"
              + "</Relationships>");
      writeZipEntry(
          zipOutputStream,
          "xl/styles.xml",
          "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
              + "<styleSheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">"
              + "<fonts count=\"1\"><font><sz val=\"11\"/><name val=\"Calibri\"/></font></fonts>"
              + "<fills count=\"1\"><fill><patternFill patternType=\"none\"/></fill></fills>"
              + "<borders count=\"1\"><border><left/><right/><top/><bottom/><diagonal/></border></borders>"
              + "<cellStyleXfs count=\"1\"><xf numFmtId=\"0\" fontId=\"0\" fillId=\"0\" borderId=\"0\"/></cellStyleXfs>"
              + "<cellXfs count=\"1\"><xf numFmtId=\"0\" fontId=\"0\" fillId=\"0\" borderId=\"0\" xfId=\"0\"/></cellXfs>"
              + "<cellStyles count=\"1\"><cellStyle name=\"Normal\" xfId=\"0\" builtinId=\"0\"/></cellStyles>"
              + "</styleSheet>");
      writeZipEntry(
          zipOutputStream,
          "xl/worksheets/sheet1.xml",
          buildGeneratedWorksheetXml(headers, rows));
      zipOutputStream.finish();
      return outputStream.toByteArray();
    } catch (IOException exception) {
      throw new IllegalStateException("Failed to generate workbook bytes.", exception);
    }
  }

  private String buildGeneratedWorksheetXml(List<String> headers, List<List<String>> rows) {
    StringBuilder rowBuilder = new StringBuilder();
    rowBuilder.append("<row r=\"1\">");
    for (int columnIndex = 0; columnIndex < headers.size(); columnIndex++) {
      rowBuilder.append(generatedInlineStringCell(1, columnIndex, headers.get(columnIndex)));
    }
    rowBuilder.append("</row>");
    for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
      List<String> row = rows.get(rowIndex);
      int excelRowNumber = rowIndex + 2;
      rowBuilder.append("<row r=\"").append(excelRowNumber).append("\">");
      for (int columnIndex = 0; columnIndex < headers.size(); columnIndex++) {
        String value = columnIndex < row.size() ? row.get(columnIndex) : "";
        rowBuilder.append(generatedInlineStringCell(excelRowNumber, columnIndex, value));
      }
      rowBuilder.append("</row>");
    }
    return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
        + "<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">"
        + "<sheetData>"
        + rowBuilder
        + "</sheetData></worksheet>";
  }

  private String generatedInlineStringCell(int rowNumber, int columnIndex, String value) {
    return "<c r=\""
        + generatedColumnName(columnIndex)
        + rowNumber
        + "\" t=\"inlineStr\"><is><t>"
        + escapeGeneratedXml(nullToEmpty(value))
        + "</t></is></c>";
  }

  private String generatedColumnName(int columnIndex) {
    StringBuilder builder = new StringBuilder();
    int current = columnIndex;
    do {
      builder.insert(0, (char) ('A' + (current % 26)));
      current = (current / 26) - 1;
    } while (current >= 0);
    return builder.toString();
  }

  private void writeZipEntry(ZipOutputStream zipOutputStream, String entryName, String content)
      throws IOException {
    ZipEntry zipEntry = new ZipEntry(entryName);
    zipOutputStream.putNextEntry(zipEntry);
    zipOutputStream.write(content.getBytes(StandardCharsets.UTF_8));
    zipOutputStream.closeEntry();
  }

  private String escapeGeneratedXml(String value) {
    return nullToEmpty(value)
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&apos;");
  }

  private String nullToEmpty(String value) {
    return value == null ? "" : value;
  }
  // jira-dev-pipeline:block:end existing-service-ArticleQueryService
}
/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.mysql.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Json;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.data.MeteringSpaceSummary;
import org.adamalang.mysql.model.metrics.MeteringMetrics;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Metering {

  public static Long getEarliestRecordTimeOfCreation(DataBase dataBase) throws Exception {
    return dataBase.transactSimple((connection) -> {
      {
        String sql = "SELECT `created` FROM `" + dataBase.databaseName + "`.`metering` ORDER BY `created` ASC LIMIT 1";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
          ResultSet rs = statement.executeQuery();
          if (rs.next()) {
            return rs.getDate(1).getTime();
          } else {
            return null;
          }
        }
      }
    });
  }

  public static void recordBatch(DataBase dataBase, String target, String batch, long time) throws Exception {
    dataBase.transactSimple((connection) -> {
      {
        String sql = "INSERT INTO `" + dataBase.databaseName + "`.`metering` (`target`, `batch`, `created`) VALUES (?,?,?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
          statement.setString(1, target);
          statement.setString(2, batch);
          statement.setString(3, DataBase.dateTimeOf(time));
          statement.execute();
        }
      }
      return null;
    });
  }

  public static HashMap<String, MeteringSpaceSummary> summarizeWindow(DataBase dataBase, MeteringMetrics metrics, long fromTime, long toTime) throws Exception {
    return dataBase.transactSimple((connection) -> {
      {
        String sql = "SELECT `target`, `batch` FROM `" + dataBase.databaseName + "`.`metering` WHERE ? <= `created` AND `created` < ?";
        HashMap<String, MeteringSpaceSummary> summary = new HashMap<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
          statement.setString(1, DataBase.dateTimeOf(fromTime));
          statement.setString(2, DataBase.dateTimeOf(toTime));
          try (ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
              String target = rs.getString(1);
              ObjectNode node = Json.parseJsonObject(rs.getString(2));
              long sampleTime = node.get("time").asLong();
              metrics.metering_batch_found.run();
              if (sampleTime < fromTime) {
                metrics.metering_batch_late.run();
              } else if (sampleTime > toTime) {
                metrics.metering_batch_early.run();
              } else {
                metrics.metering_batch_just_right.run();
              }
              ObjectNode spaces = (ObjectNode) node.get("spaces");
              Iterator<Map.Entry<String, JsonNode>> it = spaces.fields();
              while (it.hasNext()) {
                Map.Entry<String, JsonNode> sampleSpace = it.next();
                MeteringSpaceSummary spaceSum = summary.get(sampleSpace.getKey());
                if (spaceSum == null) {
                  spaceSum = new MeteringSpaceSummary();
                  summary.put(sampleSpace.getKey(), spaceSum);
                }
                spaceSum.include(target, sampleSpace.getValue());
              }
            }
          }
        }
        return summary;
      }
    });
  }

}

/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.frontend;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Json;

import java.util.TreeMap;
import java.util.regex.Pattern;

public class SpaceTemplates {
  public static class SpaceTemplate {
    public final String adama;
    public final String rxhtml;

    public SpaceTemplate(String adama, String rxhtml) {
      this.adama = adama;
      this.rxhtml = rxhtml;
    }

    public String initialRxHTML(String spaceName) {
      return rxhtml.replaceAll(Pattern.quote("$TEMPLATE_SPACE"), spaceName);
    }

    public String idearg(String spaceName) {
      ObjectNode node = Json.newJsonObject();
      node.put("adama", adama);
      node.put("rxhtml", initialRxHTML(spaceName));
      return node.toString();
    }

    public ObjectNode plan() {
      ObjectNode plan = Json.newJsonObject();
      plan.put("default", "main");
      plan.putArray("plan");
      ObjectNode main = plan.putObject("versions").putObject("main");
      main.put("main", adama);
      return plan;
    }
  }

  private final TreeMap<String, SpaceTemplate> templates;

  private SpaceTemplates() {
    this.templates = new TreeMap<>();
    // BEGIN-TEMPLATES-POPULATE
    templates.put("chat", new SpaceTemplate("@static {\n  create {\n    return true;\n  }\n\n  invent {\n    return true;\n  }\n}\n\n@connected {\n  return true;\n}\n\n// `who said `what `when\nrecord Line {\n  public principal who;\n  public string what;\n  public long when;\n}\n\n// a table will privately store messages\ntable<Line> _chat;\n\n// since we want all connected parties to\n// see everything, just reactively expose it\npublic formula chat = iterate _chat;\n\nmessage Say {\n  string what;\n}\n\n// the \"channel\" enables someone to send a message\n// bound to some code\nchannel say(Say what) {\n  // ingest the line into the chat\n  _chat <- {who:@who, what:what.what, when: Time.now()};\n\n  // since you are paying for the chat, let's cap the\n  // size to 50 total messages.\n  (iterate _chat order by when desc offset 50).delete();\n}\n","<forest>\n  <template name=\"chatter\">\n    <h2><fragment /></h2>\n    <table border=\"1\">\n    <thead><tr><th>Who</th><th>What</th><th>When</th></thead>\n    <tbody rx:iterate=\"chat\">\n      <tr>\n        <td><lookup path=\"who\" transform=\"principal.agent\" /></td>\n        <td><lookup path=\"what\" /></td>\n        <td><lookup path=\"when\" /></td>\n      </tr>\n    </tbody></table>\n    <form rx:action=\"send:say\">\n      <input type=\"text\" name=\"what\">\n      <button type=\"submit\">Say</button>\n    </form>\n  </template>\n  <page uri=\"/\">\n    <table><tr><td>\n      <connection name=\"alice\" space=\"$TEMPLATE_SPACE\" key=\"talk\" identity=\"direct:anonymous:alice\">\n        <div rx:template=\"chatter\">Alice</div>\n      </connection>\n    </td><td>\n      <connection name=\"bob\" space=\"$TEMPLATE_SPACE\" key=\"talk\" identity=\"direct:anonymous:bob\">\n        <div rx:template=\"chatter\">Bob</div>\n      </connection>\n    </td></tr></table>\n  </page>\n</forest>"));
    templates.put("hearts", new SpaceTemplate("\n@static {\n  // anyone can create\n  create { return true; }\n  invent { return true; }\n\n  // As this will spawn on demand, let's clean up when the viewer goes away\n  delete_on_close = true;\n}\n\n// we define the suit of a card\nenum Suit {\n  Clubs:1,\n  Hearts:2,\n  Spades:3,\n  Diamonds:4,\n}\n\n// the rank of a card\nenum Rank {\n  Two:2,\n  Three:3,\n  Four:4,\n  Five:5,\n  Six:6,\n  Seven:7,\n  Eight:8,\n  Nine:9,\n  Ten:10,\n  Jack:11,\n  Queen:12,\n  King:13,\n  Ace:14,\n}\n\n// where can a card be\nenum Place {\n  Deck:1,\n  Hand:2,\n  InPlay:3,\n  Taken:4\n}\n\n// model the card and its location and ownership\nrecord Card {\n  public int id;\n  public Suit suit;\n  public Rank rank;\n  private principal owner;\n  private int ordering;\n  private Place place;\n  private auto points = suit == Suit::Hearts ? 1 : (suit == Suit::Spades && rank==Rank::Queen ? 13 : 0);\n\n  // define a policy as to who can see the card\n  policy p {\n    // if it is in hand on in the pot, then only the owner of the card can see it\n    // the rules of hearts have cards face down\n    if (place == Place::Hand || place == Place::Taken) {\n      return @who == owner;\n    }\n    // if it is in the pot or in play, then anyone can see it\n    if (place==Place::InPlay) {\n      return true;\n    }\n    // otherwise, it is in the deck and thus not visible\n    return false;\n  }\n\n  method reset() {\n    ordering = Random.genInt();\n    owner = @no_one;\n    place = Place::Hand;\n  }\n\n  require p;\n}\n\n// the entire deck of cards\ntable<Card> deck;\n\n// show the player hand (and let the privacy policy filter out by person)\nbubble hand = iterate deck where place == Place::Hand where owner == @who order by id asc;\n\n// show all cards in the pot (this would be a different way of defining hand)\nbubble my_take = iterate deck where place == Place::Taken && owner == @who;\n\n// no real constructor\nmessage Empty {}\n\nprincipal owner;\n\nrecord Player {\n  public int id;\n  public principal link;\n  public int points;\n  viewer_is<link> int play_order;\n}\n\ntable<Player> players;\n\n@connected {\n  if ((iterate players where link==@who).size() > 0) {\n    return true;\n  }\n  if (players.size() < 4) {\n    players <- {\n      link:@who,\n      play_order: players.size(),\n      points:0\n    };\n    if (players.size() == 4) {\n      transition #setup;\n    }\n    return true;\n  }\n  return false;\n}\n\n// everyone in the game\npublic auto people = iterate players order by play_order;\n\n// the players by their ordering\npublic auto players_ordered = iterate players order by play_order;\n\n// are we actually playing the game?\npublic bool playing = false;\n\n// how setup the game state\n#setup {\n  // build the deck\n  foreach (s in Suit::*) {\n    foreach (r in Rank::*) {\n      deck <- {rank:r, suit:s, place:Place::Deck};\n    }\n  }\n\n  // normalize the players from 0 to 3\n  int normativeOrder = 0;\n  (iterate players order by play_order asc).play_order = normativeOrder++;\n\n  // shuffle and distribute the cards\n  transition #shuffle_and_distribute;\n}\n\nenum PassingMode { Across:0, ToLeft:1, ToRight:2, None:3 }\n\npublic PassingMode passing_mode;\n\n#shuffle_and_distribute {\n  // it may be useful to allow methods on a record, fuck\n  (iterate deck).reset();\n\n  // distribute cards to players\n  Player[] op = (iterate players order by play_order).toArray();\n  for (int k = 0; k < 4; k++) {\n    if (op[k] as player) {\n      (iterate deck where owner == @no_one order by ordering limit 13).owner = player.link;\n    }\n  }\n  transition #pass;\n}\n\nmessage CardDecision {\n  int id;\n}\n\nchannel<CardDecision[]> pass_channel;\n\n// this is wanky, need arrays at a top level that are finite to help...\nprincipal player1;\nprincipal player2;\nprincipal player3;\nprincipal player4;\n\nprincipal current;\n\n#pass {\n  if (passing_mode == PassingMode::None) {\n    transition #start_play;\n    return;\n  }\n\n  // this is wanky as fuck, and I don't like it. We have this fundamental problem of what if there are not enough players, then how does this fail...\n  // we should consider a @fatal keyword to signal that a game is just fucked\n\n  Player[] op = (iterate players order by play_order).toArray();\n  if (op[0] as player) {\n    player1 = player.link;\n  }\n  if (op[1] as player) {\n    player2 = player.link;\n  }\n  if (op[2] as player) {\n    player3 = player.link;\n  }\n  if (op[3] as player) {\n    player4 = player.link;\n  }\n  // what does an await on no_one mean, it means the whole thing is fucked\n\n  // we really need a future array since this has some awkward stuff\n  future<maybe<CardDecision[]>> pass1 = pass_channel.choose(player1, @convert<CardDecision>(iterate deck where owner==player1), 3);\n  future<maybe<CardDecision[]>> pass2 = pass_channel.choose(player2, @convert<CardDecision>(iterate deck where owner==player2), 3);\n  future<maybe<CardDecision[]>> pass3 = pass_channel.choose(player3, @convert<CardDecision>(iterate deck where owner==player3), 3);\n  future<maybe<CardDecision[]>> pass4 = pass_channel.choose(player4, @convert<CardDecision>(iterate deck where owner==player4), 3);\n\n  // the reason we do the futures above and then await them below like this is so all players can pass at the same time.\n  // the problem at hand is that the await will consume, so non-awaited futures will cause the client to sit dumbly... this can be fixed easily I think\n  // by having the make_future<> check the stream and pre-drain the queue and allow the await to short-circuit with the provide option\n\n  if (pass1.await() as decision1) {\n  if (pass2.await() as decision2) {\n  if (pass3.await() as decision3) {\n  if (pass4.await() as decision4) {\n\n  if (passing_mode == PassingMode::ToRight) {\n    foreach (dec in decision1) {\n      (iterate deck where id == dec.id).owner = player2;\n    }\n    foreach (dec in decision2) {\n      (iterate deck where id == dec.id).owner = player3;\n    }\n    foreach (dec in decision3) {\n      (iterate deck where id == dec.id).owner = player4;\n    }\n    foreach (dec in decision4) {\n      (iterate deck where id == dec.id).owner = player1;\n    }\n  } else if (passing_mode == PassingMode::ToLeft) {\n    foreach (dec in decision1) {\n      (iterate deck where id == dec.id).owner = player4;\n    }\n    foreach (dec in decision2) {\n      (iterate deck where id == dec.id).owner = player1;\n    }\n    foreach (dec in decision3) {\n      (iterate deck where id == dec.id).owner = player2;\n    }\n    foreach (dec in decision4) {\n      (iterate deck where id == dec.id).owner = player3;\n    }\n  } else if (passing_mode == PassingMode::Across) {\n    foreach (dec in decision1) {\n      (iterate deck where id == dec.id).owner = player3;\n    }\n    foreach (dec in decision2) {\n      (iterate deck where id == dec.id).owner = player4;\n    }\n    foreach (dec in decision3) {\n      (iterate deck where id == dec.id).owner = player1;\n    }\n    foreach (dec in decision4) {\n      (iterate deck where id == dec.id).owner = player2;\n    }\n  }\n\n  }}}}\n  transition #start_play;\n}\n\npublic int played = 0;\npublic Suit suit_in_play;\npublic bool points_played = false;\npublic auto in_play = iterate deck where place == Place::InPlay order by rank desc;\n\n#start_play {\n  // no cards hae been played\n  played = 0;\n  points_played = false;\n\n  // assign a player to current\n  current = player1;\n  if ( (iterate deck where rank == Rank::Two && suit == Suit::Clubs)[0] as two_clubs) {\n    current = two_clubs.owner;\n  } // otherwise, @fatal\n\n  transition #play;\n}\n\nchannel<CardDecision> single_play;\n\n// how to attribute this to a person\n\npublic principal last_winner;\n\n#play {\n  list<Card> choices = iterate deck where owner == current && place == Place::Hand && rank == Rank::Two && suit == Suit::Clubs;\n  if (choices.size() == 0) {\n    choices = iterate deck where owner==current && place == Place::Hand && (\n      played == 0 && (points_played || points == 0) ||\n      played > 0 && suit_in_play == suit\n    );\n  }\n  if (choices.size() == 0) { // anything in hand\n    choices = iterate deck where owner==current && place == Place::Hand;\n  }\n  if (choices.size() == 0) {\n    transition #score;\n    return;\n  }\n  future<maybe<CardDecision>> playX = single_play.decide(current, @convert<CardDecision>(choices));\n  if (playX.await() as dec) {\n    if ((iterate deck where id == dec.id)[0] as cardPlayed) {\n      cardPlayed.place = Place::InPlay;\n      if (cardPlayed.points > 0) {\n        points_played = true;\n      }\n      if (played == 0) {\n        suit_in_play = cardPlayed.suit;\n      }\n    }\n  }\n\n\n  // if the number of cards played is less than 4, then next player; otherwise, decide winner of pot and award points\n\n  // TODO: need finite arrays and cyclic integers\n  if (current == player1) {\n    current = player2;\n  } else if (current == player2) {\n    current = player3;\n  } else if (current == player3) {\n    current = player4;\n  } else if (current == player4) {\n    current = player1;\n  }\n\n  if (played == 3) {\n    if ( (iterate deck where place == Place::InPlay && suit == suit_in_play order by rank desc limit 1)[0] as winner) {\n      (iterate deck where place == Place::InPlay).owner = winner.owner;\n      last_winner = winner.owner;\n    }\n    (iterate deck where place == Place::InPlay).place = Place::Taken;\n    played = 0;\n    current = last_winner;\n    if( (iterate deck where owner == current && place == Place::Hand).size() == 0) {\n      transition #score;\n      return;\n    }\n  } else {\n    played++;\n  }\n  transition #play;\n}\n\npublic int points_awarded = 0;\n\n#score {\n  // award points\n  foreach(p in iterate players) {\n    int local_points = 0;\n    foreach(c in iterate deck where owner == p.link && place == Place::Taken) {\n      local_points += c.points;\n    }\n    if (local_points == 26) {\n      foreach(p2 in iterate players where link != p.link) {\n        p2.points += 26;\n        points_awarded += 26;\n      }\n    } else {\n      p.points += local_points;\n      points_awarded += local_points;\n    }\n  }\n\n  passing_mode = passing_mode.next();\n  transition #shuffle_and_distribute;\n}\n\n","<forest>\n  <shell inline>\n    <meta charset=\"UTF-8\">\n  </shell>\n  <template name=\"card\">\n        <font size=\"18\" rx:switch=\"suit\">\n            <span rx:case=\"1\">\n                <span rx:switch=\"rank\">\n                    <span rx:case=\"2\">🃒</span>\n                    <span rx:case=\"3\">🃓</span>\n                    <span rx:case=\"4\">🃔</span>\n                    <span rx:case=\"5\">🃕</span>\n                    <span rx:case=\"6\">🃖</span>\n                    <span rx:case=\"7\">🃗</span>\n                    <span rx:case=\"8\">🃘</span>\n                    <span rx:case=\"9\">🃙</span>\n                    <span rx:case=\"10\">🃚</span>\n                    <span rx:case=\"11\">🃛</span>\n                    <span rx:case=\"12\">🃝</span>\n                    <span rx:case=\"13\">🃞</span>\n                    <span rx:case=\"14\">🃑</span>\n                </span>\n            </span>\n            <span rx:case=\"2\">\n                <span rx:switch=\"rank\">\n                    <span rx:case=\"2\">🂲</span>\n                    <span rx:case=\"3\">🂳</span>\n                    <span rx:case=\"4\">🂴</span>\n                    <span rx:case=\"5\">🂵</span>\n                    <span rx:case=\"6\">🂶</span>\n                    <span rx:case=\"7\">🂷</span>\n                    <span rx:case=\"8\">🂸</span>\n                    <span rx:case=\"9\">🂹</span>\n                    <span rx:case=\"10\">🂺</span>\n                    <span rx:case=\"11\">🂻</span>\n                    <span rx:case=\"12\">🂽</span>\n                    <span rx:case=\"13\">🂾</span>\n                    <span rx:case=\"14\">🂱</span>\n                </span>\n            </span>\n            <span rx:case=\"3\">\n                <span rx:switch=\"rank\">\n                    <span rx:case=\"2\">🂢</span>\n                    <span rx:case=\"3\">🂣</span>\n                    <span rx:case=\"4\">🂤</span>\n                    <span rx:case=\"5\">🂥</span>\n                    <span rx:case=\"6\">🂦</span>\n                    <span rx:case=\"7\">🂧</span>\n                    <span rx:case=\"8\">🂨</span>\n                    <span rx:case=\"9\">🂩</span>\n                    <span rx:case=\"10\">🂪</span>\n                    <span rx:case=\"11\">🂫</span>\n                    <span rx:case=\"12\">🂭</span>\n                    <span rx:case=\"13\">🂮</span>\n                    <span rx:case=\"14\">🂡</span>\n                </span>\n            </span>\n            <span rx:case=\"4\">\n                <span rx:switch=\"rank\">\n                    <span rx:case=\"2\">🃂</span>\n                    <span rx:case=\"3\">🃃</span>\n                    <span rx:case=\"4\">🃄</span>\n                    <span rx:case=\"5\">🃅</span>\n                    <span rx:case=\"6\">🃆</span>\n                    <span rx:case=\"7\">🃇</span>\n                    <span rx:case=\"8\">🃈</span>\n                    <span rx:case=\"9\">🃉</span>\n                    <span rx:case=\"10\">🃊</span>\n                    <span rx:case=\"11\">🃋</span>\n                    <span rx:case=\"12\">🃍</span>\n                    <span rx:case=\"13\">🃎</span>\n                    <span rx:case=\"14\">🃁</span>\n                </span>\n            </span>\n        </font>\n    </template>\n    <template name=\"hearts_player\">\n        <h1>Player: <fragment /></h1>\n        <table>\n            <tbody>\n                <tr rx:iterate=\"in_play\">\n                    <td rx:template=\"card\"></td>\n                </tr>\n                <tr rx:iterate=\"hand\">\n                    <td>\n                        <lookup path=\"/passing_mode\" />\n                        <div rx:template=\"card\"></div>\n                        <div rx:if=\"choose:pass_channel\">\n                            <button rx:click=\"choose:pass_channel\">\n                                <span rx:ifnot=\"chosen:pass_channel\">\n                                    Pass <span rx:switch=\"/passing_mode\">\n                                        <span rx:case=\"0\">Across</span>\n                                        <span rx:case=\"1\">Left</span>\n                                        <span rx:case=\"2\">Right</span>\n                                        <span rx:case=\"3\">None</span>\n                                    </span>\n                                    <span rx:else>\n                                        Don't pass\n                                    </span>\n                                </span>\n                            </button>\n                        </div>\n                        <div rx:if=\"decide:single_play\">\n                            <button rx:click=\"decide:single_play\">\n                                Play\n                            </button>\n                        </div>\n                    </td>\n                </tr>\n                <tr rx:if=\"finalize:pass_channel\">\n                    <td>\n                        <button rx:click=\"finalize:pass_channel\">\n                            Finalize Choice\n                        </button>\n                    </td>\n                </tr>\n            </tbody>\n        </table>\n    </template>\n    <page uri=\"/\">\n        <div>\n            <connection name=\"alice_hearts\" space=\"$TEMPLATE_SPACE\" key=\"demo-{view:session_id}\" identity=\"direct:anonymous:alice\">\n                <div rx:template=\"hearts_player\">Alice</div>\n                <div rx:disconnected>\n                    Connecting...\n                </div>\n            </connection>\n\n            <connection name=\"bob_hearts\" space=\"$TEMPLATE_SPACE\" key=\"demo-{view:session_id}\" identity=\"direct:anonymous:bob\">\n                <div rx:template=\"hearts_player\">Bob</div>\n                <div rx:disconnected>\n                    Connecting...\n                </div>\n            </connection>\n            <connection name=\"carol_hearts\" space=\"$TEMPLATE_SPACE\" key=\"demo-{view:session_id}\" identity=\"direct:anonymous:carol\">\n                <div rx:template=\"hearts_player\">Carol</div>\n                <div rx:disconnected>\n                    Connecting...\n                </div>\n            </connection>\n            <connection name=\"dan_hearts\" space=\"$TEMPLATE_SPACE\" key=\"demo-{view:session_id}\" identity=\"direct:anonymous:dan\">\n                <div rx:template=\"hearts_player\">Dan</div>\n                <div rx:disconnected>\n                    Connecting...\n                </div>\n            </connection>\n        </div>\n    </page>\n</forest>"));
    templates.put("none", new SpaceTemplate("@static {\n  create {\n    return @who.isAnonymous() || @who.isAdamaDeveloper();\n  }\n}\n\nprivate principal owner;\n\n@construct {\n  owner = @who;\n}\n\n@connected {\n  return owner == @who;\n}\n\n@delete {\n  return owner == @who;\n}\n","<forest>\n</forest>"));
    templates.put("pubsub", new SpaceTemplate("@static {\n  // anyone can create/invent\n  create { return true; }\n  invent { return true; }\n}\n\n// let everyone connect; sure, what can go wrong\n@connected {\n  return true;\n}\n\n// let everyone delete; sure, what can go wrong\n@delete {\n  return true;\n}\n\n// we build a table of publishes with who published it and when they did so\nrecord Publish {\n  public principal who;\n  public long when;\n  public string payload;\n}\n\ntable<Publish> _publishes;\n\n// since tables are private, we expose all publishes to all connected people\npublic formula publishes = iterate _publishes order by when asc;\n\n// we wrap a payload inside a message\nmessage PublishMessage {\n  string payload;\n}\n\nprocedure expire_publishes() {\n  (iterate _publishes where when < Time.now() - 30000).delete();\n}\n\n// and then open a channel to accept the publish from any connected client\nchannel publish(PublishMessage message) {\n  _publishes <- {who: @who, when: Time.now(), payload: message.payload };\n\n  // At this point, we encounter a key problem with maintaining a\n  // log of publishes. Namely, the log is potentially infinite, so\n  // we have to leverage some product intelligence to reduce it to\n  // a reasonably finite set which is important for the product.\n\n  // First, we age out publishes too old (sad face)\n  expire_publishes();\n\n  // Second, we hard cap the publishes biasing younger ones\n  (iterate _publishes\n     order by when desc\n     offset 10).delete();\n\n  transition #clean in 1;\n}\n\n#clean {\n  expire_publishes();\n  if (_publishes.size() > 0) {\n    transition #clean in 1;\n  }\n}\n\n","<forest>\n  <template name=\"history\">\n    <h2><fragment /></h2>\n    <table border=\"1\">\n    <thead><tr><th>Who</th><th>Payload</th><th>When</th></thead>\n    <tbody rx:iterate=\"publishes\">\n      <tr>\n        <td><lookup path=\"who\" transform=\"principal.agent\" /></td>\n        <td><lookup path=\"payload\" /></td>\n        <td><lookup path=\"when\" /></td>\n      </tr>\n    </tbody></table>\n    <form rx:action=\"send:publish\">\n      <input type=\"text\" name=\"payload\">\n      <button type=\"submit\">Publish</button>\n    </form>\n  </template>\n  <page uri=\"/\">\n    <table><tr><td>\n      <connection name=\"alice\" space=\"$TEMPLATE_SPACE\" key=\"demo-log\" identity=\"direct:anonymous:alice\">\n        <div rx:template=\"history\">Alice's View</div>\n      </connection>\n    </td><td>\n      <connection name=\"bob\" space=\"$TEMPLATE_SPACE\" key=\"demo-log\" identity=\"direct:anonymous:bob\">\n        <div rx:template=\"history\">Bob's View</div>\n      </connection>\n    </td></tr></table>\n  </page>\n</forest>"));
    templates.put("tic-tac-toe", new SpaceTemplate("@static {\n  // As this is going to be a live home-page sample, let anyone create\n  create { return true; }\n  invent { return true; }\n\n  // As this will spawn on demand, let's clean up when the viewer goes away\n  delete_on_close = true;\n}\n\n// What is the state of a square\nenum SquareState { Open, X, O }\n\n// who are the two players\npublic principal playerX;\npublic principal playerO;\n\n// who is the current player\npublic principal current;\n\n// how many wins per player\npublic int wins_X;\npublic int wins_O;\n\n// how many stalemates\npublic int stalemates;\n\n// personalized data for the connected player:\n// show the player their role, a signal if it is their turn, and their wins\nbubble your_role = playerX == @who ? \"X\" : (playerO == @who ? \"O\" : \"Observer\");\nbubble your_turn = current == @who;\nbubble your_wins = playerX == @who ? wins_X : (playerO == @who ? wins_O : 0);\n\n// a record of the data in the square\nrecord Square {\n  public int id;\n  public int x;\n  public int y;\n  public SquareState state;\n}\n\n// the collection of all squares\ntable<Square> _squares;\n\n// for visualization, we break the squares into rows\npublic formula row1 = iterate _squares where y == 0;\npublic formula row2 = iterate _squares where y == 1;\npublic formula row3 = iterate _squares where y == 2;\n\n// when the document is created, initialize the squares and zero out the totals\n@construct {\n  for (int y = 0; y < 3; y++) {\n    for (int x = 0; x < 3; x++) {\n      _squares <- { x:x, y:y, state: SquareState::Open };\n    }\n  }\n  wins_X = 0;\n  wins_O = 0;\n  stalemates = 0;\n}\n\n// when a player connects, assign them to either the X or O role. If there are more than two players, then they can observe.\n@connected {\n  if (playerX == @no_one) {\n    playerX = @who;\n    if (playerO != @no_one) {\n      transition #initiate;\n    }\n  } else if (playerO == @no_one) {\n    playerO = @who;\n    if (playerX != @no_one) {\n      transition #initiate;\n    }\n  }\n  return true;\n}\n\n// open a channel for players to select a move\nmessage Play { int id; }\nchannel<Play> play;\n\n// the game is afoot\n#initiate {\n  current = playerX;\n  transition #turn;\n}\n\n// test if the placed square produced a winning combination\nprocedure test_placed_for_victory(SquareState placed) -> bool {\n  for (int k = 0; k < 3; k++) {\n    // vertical lines\n    if ( (iterate _squares where x == k && state == placed).size() == 3) {\n      return true;\n    }\n    // horizontal lines\n    if ( (iterate _squares where y == k && state == placed).size() == 3) {\n      return true;\n    }\n  }\n  // diagonals\n  if ( (iterate _squares where y == x && state == placed).size() == 3 || (iterate _squares where y == 2 - x && state == placed).size() == 3 ) {\n    return true;\n  }\n  return false;\n}\n\n#turn {\n  // find the open spaces\n  list<Square> open = iterate _squares where state == SquareState::Open;\n  if (open.size() == 0) {\n    stalemates++;\n    transition #end;\n    return;\n  }\n  // ask the current play to choose an open space\n  if (play.decide(current, @convert<Play>(open)).await() as pick) {\n    // assign the open space to the player\n    let placed = playerX == current ? SquareState::X : SquareState::O;;\n    (iterate _squares where id == pick.id).state = placed;\n    if (test_placed_for_victory(placed)) {\n      if (playerX == current) {\n        wins_X++;\n      } else {\n        wins_O++;\n      }\n      transition #end;\n    } else {\n      transition #turn;\n    }\n    current = playerX == current ? playerO : playerX;\n  }\n}\n\n#end {\n  (iterate _squares).state = SquareState::Open;\n  transition #turn;\n}\n","<forest>\n  <template name=\"cell\">\n    <span rx:switch=\"state\">\n      <span rx:case=\"0\">\n        <span rx:if=\"decide:play\">\n          <button rx:click=\"decide:play\">Play</button>\n        </span>\n      </span>\n      <span rx:case=\"1\">X</span>\n      <span rx:case=\"2\">O</span>\n    </span>\n  </template>\n\n  <template name=\"game\">\n    <h2><fragment /> - <lookup path=\"your_role\" /> (Won: <lookup path=\"your_wins\" />)</h2>\n    <table border=\"1\">\n      <tr rx:iterate=\"row1\">\n        <td rx:template=\"cell\"></td>\n      </tr>\n      <tr rx:iterate=\"row2\">\n        <td rx:template=\"cell\"></td>\n      </tr>\n      <tr rx:iterate=\"row3\">\n        <td rx:template=\"cell\"></td>\n      </tr>\n    </table>\n  </template>\n  <page uri=\"/\">\n    <connection name=\"player1\" identity=\"direct:anonymous:alice\" space=\"$TEMPLATE_SPACE\" key=\"demo-{view:session_id}\">\n      <div rx:template=\"game\">Alice</div>\n    </connection>\n    <hr />\n    <connection name=\"player2\" identity=\"direct:anonymous:bob\" space=\"$TEMPLATE_SPACE\" key=\"demo-{view:session_id}\">\n      <div rx:template=\"game\">Bob</div>\n    </connection>\n  </page>\n</forest>"));
    // END-TEMPLATES-POPULATE
  }

  public SpaceTemplate of(String name) {
    SpaceTemplate bundle = templates.get(name == null ? "none" : name);
    if (bundle != null) {
      return bundle;
    }
    return templates.get("none");
  }

  public static final SpaceTemplates REGISTRY = new SpaceTemplates();
}

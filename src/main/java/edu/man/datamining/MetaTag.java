package edu.man.datamining;

import se.sics.prologbeans.PrologSession;
import gov.nih.nlm.nls.metamap.*;

import edu.man.util.Pair;
import edu.man.util.LevenshteinDistance;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.List;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
/**
 * MetaTag: Wrapper over the metamap api class used to
 * tag terms or phrases with semantic types.
 */

class MetaTag {
  private MetaMapApi api;

  public MetaTag(String host, int port) throws IOException {
    api = new MetaMapApiImpl();
    api.setHost(host);
    api.setPort(port);
    InputStream in = getClass().getClassLoader().getResourceAsStream("filtered_semtypes.txt");
    BufferedReader br = new BufferedReader( new InputStreamReader(in));
    String line;
    String semtypes = "";
    while ((line = br.readLine()) != null) {
      if (line.startsWith("{") || line.startsWith("[")) continue;
      String[] res = line.split("\\|");
      semtypes = semtypes+res[0]+",";
    }
    api.setOptions("-az -J "+semtypes);
    api.getSession().setAutoConnect(true);
  }

  private String process(String s) {
    return s.replaceAll("(?x) (?<= \\pL ) (?= \\pN ) | (?<= \\pN ) (?= \\pL )", " ");
  }

  private List merge_unique(List A, List B) {
    return ListUtils.subtract(ListUtils.union(A, B), ListUtils.intersection(A, B));
  }

  public void disconnect() {
    api.getSession().disconnect();
  }

  private void processEv(Ev ev, JSONObject matches) throws Exception{
    String key_mw = StringUtils.join(ev.getMatchedWords(), " ");
    String key_cn = ev.getConceptName();
    JSONObject matchedWordsObj = matches.has(key_mw) ? (JSONObject) matches.get(key_mw) : new JSONObject();
    ArrayList value = matchedWordsObj.has(key_cn) 
                      ? (ArrayList) merge_unique((List) matchedWordsObj.get(key_cn), ev.getSemanticTypes())
                      : (ArrayList) ev.getSemanticTypes();
    matchedWordsObj.put(key_cn, value);
    matches.put(key_mw, matchedWordsObj);
  }

  public JSONObject getSemTypes(String phrase, boolean includeCandidates, int scoreThreshold) throws Exception {
    String query = process(phrase);
    System.out.println("Calling api.");
    List<Result> resultList = api.processCitationsFromString((String) query);
    JSONObject matches = new JSONObject();
    System.out.println("Parsing results...");
    
    for (Result result: resultList) {
    for (Utterance utterance: result.getUtteranceList()) {
    for (PCM pcm: utterance.getPCMList()) {
      if (includeCandidates) { 
        for (Ev cEv: pcm.getCandidateList()) {
          if (cEv.getScore() > scoreThreshold) continue;
          processEv(cEv, matches);
        }
      }
      for (Mapping map: pcm.getMappingList()) 
      for (Ev mapEv: map.getEvList()) 
      processEv(mapEv, matches);
    }}}
    return matches;
  }
}

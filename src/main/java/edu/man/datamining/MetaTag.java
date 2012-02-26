package edu.man.datamining;

import se.sics.prologbeans.PrologSession;
import gov.nih.nlm.nls.metamap.*;
import edu.man.util.Pair;
import edu.man.util.LevenshteinDistance;

/**
 * MetaTag: Wrapper over the metamap api class used to
 * tag terms or phrases with semantic types.
 */

class MetaTag {
  private MetaMapApi api;

  public MetaTag(String host, int port) {
    api = new MetaMapApiImpl();
    api.setHost(host);
    api.setPort(port);
    System.out.println(LevenshteinDistance.get("mama", "are"));
  }
}

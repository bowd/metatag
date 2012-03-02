package edu.man.datamining;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.io.StringReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;

import edu.stanford.nlp.objectbank.TokenizerFactory;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import org.yaml.snakeyaml.*;

public class SPWorker {
  private LexicalizedParser lp;

  public SPWorker() {
    lp = new LexicalizedParser("/fs/linserver/data1/mbax9bd2/sources/stanford-parser-2012-02-03/grammar/englishPCFG.ser.gz");
  }

  public static void main(String[] args) {
    SPWorker app = new SPWorker();
    BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
    while(true) {
      String input = stdin.readLine();
      String parsedText = "";

      for (List<HasWord> sentence : new DocumentPreprocessor(new StringReader((String) input))) {
        Tree pTree = lp.apply(sentence);
        parsedText += pTree.toString() + SENTENCE_DELIMITER; 
      }

      System.out.println(parsedText);
    }
  }
}

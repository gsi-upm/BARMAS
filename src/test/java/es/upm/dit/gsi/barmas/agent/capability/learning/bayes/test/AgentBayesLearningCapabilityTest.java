/**
 * es.upm.dit.gsi.barmas.agent.capability.learning.bayes.test.AgentBayesLearningCapabilityTest.java
 */
package es.upm.dit.gsi.barmas.agent.capability.learning.bayes.test;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import unbbayes.prs.bn.ProbabilisticNetwork;
import es.upm.dit.gsi.barmas.agent.capability.learning.bayes.AgentBayesLearningCapability;
import es.upm.dit.gsi.barmas.agent.capability.learning.bayes.BayesLearningAgent;
import es.upm.dit.gsi.shanks.agent.capability.reasoning.bayes.BayesianReasonerShanksAgent;
import es.upm.dit.gsi.shanks.agent.capability.reasoning.bayes.ShanksAgentBayesianReasoningCapability;

/**
 * Project: barmas File:
 * es.upm.dit.gsi.barmas.agent.capability.learning.bayes.test
 * .AgentBayesLearningCapabilityTest.java
 * 
 * Grupo de Sistemas Inteligentes Departamento de Ingeniería de Sistemas
 * Telemáticos Universidad Politécnica de Madrid (UPM)
 * 
 * @author alvarocarrera
 * @email a.carrera@gsi.dit.upm.es
 * @twitter @alvarocarrera
 * @date 29/10/2013
 * @version 0.1
 * 
 */
public class AgentBayesLearningCapabilityTest {

	private Logger logger = Logger
			.getLogger(AgentBayesLearningCapabilityTest.class.getName());

	private interface MyBayesLearningAgent extends BayesLearningAgent,
			BayesianReasonerShanksAgent {

	}

	MyBayesLearningAgent agent;

	/**
	 * @throws Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		LogManager lm = LogManager.getLogManager();
		File configFile = new File("src/test/resources/logging.properties");
		lm.readConfiguration(new FileInputStream(configFile));
	}

	/**
	 * @throws Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		agent = new MyBayesLearningAgent() {

			ProbabilisticNetwork bn;

			@Override
			public String getDatasetFile() {
				return "src/test/resources/agentdataset-central.csv";
			}

			@Override
			public String getBNOutputFile() {
				return "src/test/resources/agentBN-central.net";
			}

			@Override
			public ProbabilisticNetwork getBayesianNetwork() {
				return bn;
			}

			@Override
			public void setBayesianNetwork(ProbabilisticNetwork bn) {
				this.bn = bn;
			}

			@Override
			public String getBayesianNetworkFilePath() {
				return this.getBNOutputFile();
			}

			@Override
			public Logger getLogger() {
				return logger;
			}
		};
	}

	/**
	 * @throws Exception
	 */
	@After
	public void tearDown() throws Exception {
		File f = new File(agent.getBNOutputFile());
		f.delete();
	}

	@Test
	public void learningWithOutputTest() {
		AgentBayesLearningCapability.learnBNWithBayesianSearch(agent);
		File f = new File(agent.getBNOutputFile());
		Assert.assertTrue(f.exists());
	}

	@Test
	public void learningAndReasoningTest() {
		try {
			AgentBayesLearningCapability.learnBNWithBayesianSearch(agent);
			ShanksAgentBayesianReasoningCapability.loadNetwork(agent);
			HashMap<String, HashMap<String, Float>> hyps = ShanksAgentBayesianReasoningCapability
					.getAllHypotheses(agent);
			for (Entry<String, HashMap<String, Float>> hyp : hyps.entrySet()) {
				float total = 0;
				for (Entry<String, Float> values : hyp.getValue().entrySet()) {
					float value = values.getValue();
					total = total + value;
					if (value > 1 || value < 0) {
						Assert.fail();
					}
				}
				Assert.assertEquals(1.0, total, 0.01);
			}
		} catch (Exception e) {
			logger.warning("Exception reasoning with learnt BN -> ");
			logger.warning(e.getMessage());
		}
	}

	@Test
	public void learningAndReasoningWithEvidencesTest() {
		try {
			AgentBayesLearningCapability.learnBNWithBayesianSearch(agent);
			ShanksAgentBayesianReasoningCapability.loadNetwork(agent);
			ShanksAgentBayesianReasoningCapability.addEvidence(
					agent.getBayesianNetwork(), "LargestSpotSize", "R");
			ShanksAgentBayesianReasoningCapability.addEvidence(
					agent.getBayesianNetwork(), "SpotDistribution", "O");
			HashMap<String, HashMap<String, Float>> hyps = ShanksAgentBayesianReasoningCapability
					.getAllHypotheses(agent);
			for (Entry<String, HashMap<String, Float>> hyp : hyps.entrySet()) {
				float total = 0;
				for (Entry<String, Float> value : hyp.getValue().entrySet()) {
					float conf = value.getValue();
					total = total + conf;
					if (conf > 1.01 || conf < -0.01) {
						Assert.fail("Incoherent Belief value: " + conf);
					}
				}
				Assert.assertEquals(1.0, total, 0.01);
			}
		} catch (Exception e) {
			logger.warning("Exception reasoning with learnt BN -> ");
			logger.warning(e.getMessage());
		}
	}
//
//	@Test
//	public void ComparingLearningWithoutEvidencesTest() {
//		try {
//			AgentBayesLearningCapability.learnBNWithBayesianSearch(agent);
//			ShanksAgentBayesianReasoningCapability.loadNetwork(agent);
//			HashMap<String, HashMap<String, Float>> hyps1 = ShanksAgentBayesianReasoningCapability
//					.getAllHypotheses(agent);
//			AgentBayesLearningCapability.learnBNWithBayesianSearch(agent);
//			ShanksAgentBayesianReasoningCapability.loadNetwork(agent);
//			HashMap<String, HashMap<String, Float>> hyps2 = ShanksAgentBayesianReasoningCapability
//					.getAllHypotheses(agent);
//			for (Entry<String, HashMap<String, Float>> hyp : hyps1.entrySet()) {
//				HashMap<String, Float> values2 = hyps2.get(hyp.getKey());
//				for (Entry<String, Float> value : hyp.getValue().entrySet()) {
//					float value2 = values2.get(value.getKey());
//					Assert.assertEquals(value.getValue(), value2, 0.10);
//				}
//			}
//		} catch (Exception e) {
//			logger.warning("Exception reasoning with learnt BN -> ");
//			logger.warning(e.getMessage());
//		}
//
//	}
//
//	@Test
//	public void ComparingLearningTest() {
//		try {
//			AgentBayesLearningCapability.learnBNWithBayesianSearch(agent);
//			ShanksAgentBayesianReasoningCapability.loadNetwork(agent);
//			ShanksAgentBayesianReasoningCapability.addEvidence(
//					agent.getBayesianNetwork(), "LargestSpotSize", "R");
//			ShanksAgentBayesianReasoningCapability.addEvidence(
//					agent.getBayesianNetwork(), "SpotDistribution", "O");
//			HashMap<String, HashMap<String, Float>> hyps1 = ShanksAgentBayesianReasoningCapability
//					.getAllHypotheses(agent);
//			AgentBayesLearningCapability.learnBNWithBayesianSearch(agent);
//			ShanksAgentBayesianReasoningCapability.loadNetwork(agent);
//			ShanksAgentBayesianReasoningCapability.addEvidence(
//					agent.getBayesianNetwork(), "LargestSpotSize", "R");
//			ShanksAgentBayesianReasoningCapability.addEvidence(
//					agent.getBayesianNetwork(), "SpotDistribution", "O");
//			HashMap<String, HashMap<String, Float>> hyps2 = ShanksAgentBayesianReasoningCapability
//					.getAllHypotheses(agent);
//			for (Entry<String, HashMap<String, Float>> hyp : hyps1.entrySet()) {
//				HashMap<String, Float> values2 = hyps2.get(hyp.getKey());
//				for (Entry<String, Float> value : hyp.getValue().entrySet()) {
//					float value2 = values2.get(value.getKey());
//					Assert.assertEquals(value.getValue(), value2, 0.10);
//				}
//			}
//		} catch (Exception e) {
//			logger.warning("Exception reasoning with learnt BN -> ");
//			logger.warning(e.getMessage());
//		}
//
//	}
}

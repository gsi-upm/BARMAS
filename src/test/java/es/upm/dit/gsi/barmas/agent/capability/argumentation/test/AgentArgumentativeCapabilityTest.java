/*******************************************************************************
 * Copyright (c) 2013 alvarocarrera Grupo de Sistemas Inteligentes - Universidad Politécnica de Madrid. (GSI-UPM)
 * http://www.gsi.dit.upm.es/
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * 
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 * 
 * Contributors:
 *     alvarocarrera - initial API and implementation
 ******************************************************************************/
/**
 * es.upm.dit.gsi.barmas.agent.capability.argumentation.test.AgentArgumentativeCapabilityTest.java
 */
package es.upm.dit.gsi.barmas.agent.capability.argumentation.test;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import unbbayes.prs.bn.ProbabilisticNetwork;
import es.upm.dit.gsi.barmas.agent.capability.argumentation.AgentArgumentativeCapability;
import es.upm.dit.gsi.barmas.agent.capability.argumentation.ArgumentativeAgent;
import es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes.Argument;
import es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes.Proposal;
import es.upm.dit.gsi.barmas.agent.capability.argumentation.manager.ArgumentationManagerAgent;
import es.upm.dit.gsi.shanks.agent.capability.reasoning.bayes.BayesianReasonerShanksAgent;
import es.upm.dit.gsi.shanks.agent.capability.reasoning.bayes.ShanksAgentBayesianReasoningCapability;
import es.upm.dit.gsi.shanks.agent.capability.reasoning.bayes.exception.UnknowkNodeStateException;
import es.upm.dit.gsi.shanks.agent.capability.reasoning.bayes.exception.UnknownNodeException;
import es.upm.dit.gsi.shanks.exception.ShanksException;

/**
 * Project: barmas File:
 * es.upm.dit.gsi.barmas.agent.capability.argumentation.test
 * .AgentArgumentativeCapabilityTest.java
 * 
 * Grupo de Sistemas Inteligentes Departamento de Ingeniería de Sistemas
 * Telemáticos Universidad Politécnica de Madrid (UPM)
 * 
 * @author alvarocarrera
 * @email a.carrera@gsi.dit.upm.es
 * @twitter @alvarocarrera
 * @date 24/07/2013
 * @version 0.1
 * 
 */
public class AgentArgumentativeCapabilityTest {

	private Logger logger = Logger
			.getLogger(AgentArgumentativeCapabilityTest.class.getName());

	private interface MyArgumentativeAgent extends BayesianReasonerShanksAgent,
			ArgumentativeAgent {

	}

	MyArgumentativeAgent agent;

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
		agent = new MyArgumentativeAgent() {
			ProbabilisticNetwork bayes;

			public String getBayesianNetworkFilePath() {
				return "src/test/resources/alarm.net";
			}

			public ProbabilisticNetwork getBayesianNetwork() {
				return bayes;
			}

			public void setBayesianNetwork(ProbabilisticNetwork bn) {
				bayes = bn;
			}

			public String getProponentName() {
				return "TestAgent";
			}

			public ArgumentativeAgent getProponent() {
				return this;
			}

			public ArgumentationManagerAgent getArgumentationManager() {
				return null;
			}

			public String getArgumentationManagerName() {
				return null;
			}

			public Set<Argument> getCurrentArguments() throws ShanksException {
				return AgentArgumentativeCapability.createArguments(agent);
			}

			public void updateBeliefsWithNewArguments(Set<Argument> args)
					throws ShanksException {
				AgentArgumentativeCapability.updateBeliefs(args, agent);
			}

			public void setArgumentationManager(
					ArgumentationManagerAgent manager) {
				// TODO Auto-generated method stub
				
			}

			public void sendArgument(Argument arg) {
				// TODO Auto-generated method stub
				
			}
		};
		ShanksAgentBayesianReasoningCapability.loadNetwork(agent);
	}

	/**
	 * @throws Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void createArgumentsFromBN() {
		try {
			String nodeName = "MinVol";
			String status = "High";
			ShanksAgentBayesianReasoningCapability.addEvidence(
					agent.getBayesianNetwork(), nodeName, status);
			String queryNodeName = "TPR";
			String queryStatus = "Normal";
			float value = ShanksAgentBayesianReasoningCapability.getHypothesis(
					agent.getBayesianNetwork(), queryNodeName, queryStatus);
			Assert.assertEquals(0.3961F, value, 0.001F);
			queryNodeName = "ArtCO2";
			queryStatus = "Normal";
			value = ShanksAgentBayesianReasoningCapability.getHypothesis(
					agent.getBayesianNetwork(), queryNodeName, queryStatus);
			Assert.assertEquals(0.2322F, value, 0.001F);

			Set<Argument> args = agent.getCurrentArguments();
			for (Argument arg : args) {
				Assert.assertTrue(arg.getGivens().size() == 1);
				for (Proposal p : arg.getProposals()) {
					Assert.assertNotSame("MinVol", p.getNode());
					if (p.getNode().equals("TPR")
							&& p.getValues().contains("Normal")) {
						double d = p.getConfidenceForValue("Normal");
						Assert.assertEquals(0.3961F, d, 0.001F);
					} else if (p.getNode().equals("ArtCO2")
							&& p.getValues().contains("Normal")) {
						double d = p.getConfidenceForValue("Normal");
						Assert.assertEquals(0.2322F, d, 0.001F);
					}
				}
			}
			logger.finer("Number of arguments: " + args.size());
			Assert.assertTrue(args.size() == 101);
		} catch (UnknowkNodeStateException e) {
			e.printStackTrace();
			Assert.fail();
		} catch (UnknownNodeException e) {
			e.printStackTrace();
			Assert.fail();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	@Test
	public void checkNumberOfArguments() {
		try {
			String nodeName = "MinVol";
			String status = "High";
			ShanksAgentBayesianReasoningCapability.addEvidence(
					agent.getBayesianNetwork(), nodeName, status);
			nodeName = "TPR";
			status = "Normal";
			ShanksAgentBayesianReasoningCapability.addEvidence(
					agent.getBayesianNetwork(), nodeName, status);

			Set<Argument> args = agent.getCurrentArguments();
			for (Argument arg : args) {
				Assert.assertTrue(arg.getGivens().size() == 2);
			}
			logger.finer("Number of arguments: " + args.size());
			Assert.assertTrue(args.size() == 98);
		} catch (UnknowkNodeStateException e) {
			e.printStackTrace();
			Assert.fail();
		} catch (UnknownNodeException e) {
			e.printStackTrace();
			Assert.fail();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	@Test
	public void updateBeliefs() {
		try {
			String nodeName = "MinVol";
			String status = "High";
			ShanksAgentBayesianReasoningCapability.addEvidence(
					agent.getBayesianNetwork(), nodeName, status);
			String queryNodeName = "TPR";
			String queryStatus = "Normal";
			float value = ShanksAgentBayesianReasoningCapability.getHypothesis(
					agent.getBayesianNetwork(), queryNodeName, queryStatus);
			Assert.assertEquals(0.3961F, value, 0.001F);
			queryNodeName = "ArtCO2";
			queryStatus = "Normal";
			value = ShanksAgentBayesianReasoningCapability.getHypothesis(
					agent.getBayesianNetwork(), queryNodeName, queryStatus);
			Assert.assertEquals(0.2322F, value, 0.001F);

			HashMap<String, Double> artco2 = new HashMap<String, Double>();
			HashMap<String, Double> tpr = new HashMap<String, Double>();

			Set<Argument> args = agent.getCurrentArguments();
			for (Argument arg : args) {
				Assert.assertTrue(arg.getGivens().size() == 1);
				for (Proposal p : arg.getProposals()) {
					Assert.assertNotSame("MinVol", p.getNode());
					if (p.getNode().equals("TPR")) {
						for (String s : p.getValues()) {
							if (!tpr.containsKey(s)) {
								tpr.put(s, p.getConfidenceForValue(s));
							}
						}
					} else if (p.getNode().equals("ArtCO2")) {
						for (String s : p.getValues()) {
							if (!artco2.containsKey(s)) {
								artco2.put(s, p.getConfidenceForValue(s));
							}
						}
					}
				}
			}
			double total = 0;
			logger.finer("ArtCO2 values: ");
			for (Entry<String,Double> entry : artco2.entrySet()) {
				total += entry.getValue();
				logger.finer(entry.getKey()+"="+entry.getValue());
			}
			Assert.assertEquals(1, total, 0.01);
			total = 0;
			logger.finer("TPR values: ");
			for (Entry<String,Double> entry : tpr.entrySet()) {
				total += entry.getValue();
				logger.finer(entry.getKey()+"="+entry.getValue());
			}
			Assert.assertEquals(1, total, 0.01);
			
			args = new HashSet<Argument>();
			Argument arg = new Argument();
			Proposal p = new Proposal("TPR");
			p.addValueWithConfidence("Low", 0.3);
			p.addValueWithConfidence("Normal", 0.6);
			p.addValueWithConfidence("High", 0.1);
			arg.addProposal(p);
			
			p = new Proposal("ArtCO2");
			p.addValueWithConfidence("Low", 0.7);
			p.addValueWithConfidence("Normal", 0.2);
			p.addValueWithConfidence("High", 0.1);
			arg.addProposal(p);
			
			args.add(arg);
			
			AgentArgumentativeCapability.updateBeliefs(args, agent);
			

			queryNodeName = "TPR";
			queryStatus = "Low";
			value = ShanksAgentBayesianReasoningCapability.getHypothesis(
					agent.getBayesianNetwork(), queryNodeName, queryStatus);
			Assert.assertEquals(0.3F, value, 0.01F);
			queryStatus = "Normal";
			value = ShanksAgentBayesianReasoningCapability.getHypothesis(
					agent.getBayesianNetwork(), queryNodeName, queryStatus);
			Assert.assertEquals(0.6F, value, 0.01F);
			queryStatus = "High";
			value = ShanksAgentBayesianReasoningCapability.getHypothesis(
					agent.getBayesianNetwork(), queryNodeName, queryStatus);
			Assert.assertEquals(0.1F, value, 0.01F);
			queryNodeName = "ArtCO2";
			queryStatus = "Low";
			value = ShanksAgentBayesianReasoningCapability.getHypothesis(
					agent.getBayesianNetwork(), queryNodeName, queryStatus);
			Assert.assertEquals(0.7F, value, 0.01F);
			queryStatus = "Normal";
			value = ShanksAgentBayesianReasoningCapability.getHypothesis(
					agent.getBayesianNetwork(), queryNodeName, queryStatus);
			Assert.assertEquals(0.2F, value, 0.01F);
			queryStatus = "High";
			value = ShanksAgentBayesianReasoningCapability.getHypothesis(
					agent.getBayesianNetwork(), queryNodeName, queryStatus);
			Assert.assertEquals(0.1F, value, 0.01F);
			
		} catch (UnknowkNodeStateException e) {
			e.printStackTrace();
			Assert.fail();
		} catch (UnknownNodeException e) {
			e.printStackTrace();
			Assert.fail();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

}

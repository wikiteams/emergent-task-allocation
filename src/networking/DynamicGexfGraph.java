package networking;

import it.uniroma1.dis.wsngroup.gexf4j.core.EdgeType;
import it.uniroma1.dis.wsngroup.gexf4j.core.Gexf;
import it.uniroma1.dis.wsngroup.gexf4j.core.Graph;
import it.uniroma1.dis.wsngroup.gexf4j.core.Mode;
import it.uniroma1.dis.wsngroup.gexf4j.core.data.Attribute;
import it.uniroma1.dis.wsngroup.gexf4j.core.data.AttributeClass;
import it.uniroma1.dis.wsngroup.gexf4j.core.data.AttributeList;
import it.uniroma1.dis.wsngroup.gexf4j.core.data.AttributeType;
import it.uniroma1.dis.wsngroup.gexf4j.core.dynamic.TimeFormat;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.GexfImpl;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.StaxGraphWriter;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.data.AttributeListImpl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Calendar;

import collaboration.Agent;
import collaboration.Task;

public class DynamicGexfGraph {

	private Gexf gexf;
	private Graph graph;

	public DynamicGexfGraph() {
		gexf = new GexfImpl();
	}

	public void write() {
		StaxGraphWriter graphWriter = new StaxGraphWriter();
		File f = new File("dynamic_graph_contributions.gexf");
		Writer out;
		try {
			out = new FileWriter(f, false);
			graphWriter.writeToStream(gexf, out, "UTF-8");
			System.out.println(f.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public DynamicGexfGraph init() {
		Calendar date = Calendar.getInstance();

		gexf.getMetadata().setLastModified(date.getTime())
				.setCreator("wikiteams/emergent-task-allocation")
				.setDescription("Agent and task network");

		graph = gexf.getGraph();
		graph.setDefaultEdgeType(EdgeType.UNDIRECTED).setMode(Mode.DYNAMIC)
				.setTimeType(TimeFormat.XSDDATETIME);

		AttributeList attrList = new AttributeListImpl(AttributeClass.NODE);
		AttributeList attrEdgeList = new AttributeListImpl(AttributeClass.EDGE);
		graph.getAttributeLists().add(attrList);

		Attribute attName = attrList.createAttribute("0", AttributeType.STRING,
				"name");
		Attribute attSkill = attrList.createAttribute("1",
				AttributeType.STRING, "skill");

		Attribute attEdgeTime = attrEdgeList.createAttribute("0",
				AttributeType.INTEGER, "tick");
		
		return this;
	}
	
	public void addEdge(Agent agent, Task task){
		//graph.get
	}

}

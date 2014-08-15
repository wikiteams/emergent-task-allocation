package collaboration;

import repast.simphony.context.Context;
import repast.simphony.dataLoader.ContextBuilder;

public class CollaborationBuilder implements ContextBuilder<Object> {

	@Override
	public Context build(Context<Object> context) {
		context.setId("emergent-task-allocation");
		
		int agents = 5;
		for(int i = 0 ; i < agents ; i++){
			context.add(new Agent());
		}
		
		int tasks = 5;
		for(int i = 0 ; i < agents ; i++){
			context.add(new Task());
		}
		
		return context;
	}

}

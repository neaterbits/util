package com.neaterbits.util.concurrency.dependencyresolution.executor;

public final class Producers<TARGET> {

	private final ProduceFromSubTargets<TARGET> produceFromSubTargets;
	private final ProduceFromSubProducts<TARGET> produceFromSubProducts;
	
	public Producers(
	        ProduceFromSubTargets<TARGET> produceFromSubTargets,
	        ProduceFromSubProducts<TARGET> produceFromSubProducts) {
		
		this.produceFromSubTargets = produceFromSubTargets;
		this.produceFromSubProducts = produceFromSubProducts;
	}

	public ProduceFromSubTargets<TARGET> getProduceFromSubTargets() {
		return produceFromSubTargets;
	}

	public ProduceFromSubProducts<TARGET> getProduceFromSubProducts() {
		return produceFromSubProducts;
	}
}

package com.neaterbits.util.concurrency.dependencyresolution.executor;

public final class Collectors<TARGET> {

	private final CollectSubTargets<TARGET> collectSubTargets;
	private final CollectSubProducts<TARGET> collectSubProducts;
	
	public Collectors(CollectSubTargets<TARGET> collectSubTargets, CollectSubProducts<TARGET> collectSubProducts) {
		
		this.collectSubTargets = collectSubTargets;
		this.collectSubProducts = collectSubProducts;
	}

	public CollectSubTargets<TARGET> getCollectSubTargets() {
		return collectSubTargets;
	}

	public CollectSubProducts<TARGET> getCollectSubProducts() {
		return collectSubProducts;
	}
}

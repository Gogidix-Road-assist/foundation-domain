package com.gogidix.rapidassist.ai.optimization.domain.model;

/**
 * Enumeration representing the optimization algorithm type.
 */
public enum OptimizationAlgorithm {
    /**
     * Genetic Algorithm - evolutionary computation.
     */
    GENETIC_ALGORITHM,

    /**
     * Simulated Annealing - probabilistic technique.
     */
    SIMULATED_ANNEALING,

    /**
     * Bayesian Optimization - sequential model-based optimization.
     */
    BAYESIAN_OPTIMIZATION,

    /**
     * Grid Search - exhaustive search through parameter space.
     */
    GRID_SEARCH,

    /**
     * Random Search - random sampling of parameter space.
     */
    RANDOM_SEARCH,

    /**
     * Particle Swarm Optimization - population-based stochastic optimization.
     */
    PARTICLE_SWARM,

    /**
     * Gradient Descent - first-order iterative optimization.
     */
    GRADIENT_DESCENT,

    /**
     * Adam Optimizer - adaptive moment estimation.
     */
    ADAM,

    /**
     * Custom optimization algorithm.
     */
    CUSTOM
}

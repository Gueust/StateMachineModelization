package engine;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import abstractGraph.AbstractGlobalState;
import abstractGraph.AbstractState;
import abstractGraph.AbstractStateMachine;
import abstractGraph.AbstractTransition;

public class SplittingModelChecker<GS extends AbstractGlobalState<M, S, T, ?>, M extends AbstractStateMachine<S, T>, S extends AbstractState<T>, T extends AbstractTransition<S>> {

  private ModelChecker<GS, M, S, T> model_checker;

  /** The states to explore */
  protected Set<GS> unvisited_states = new HashSet<GS>();

  public SplittingModelChecker() {
    super();
    this.model_checker = new ModelChecker<GS, M, S, T>();
  }

  /**
   * Initialize the initial states as the ones contained in `init`.
   * It does not take the given collection but creates and underlying HashMap
   * containing the elements of `init`.
   * 
   * All previously added initial states are removed.
   * 
   * @param init
   */
  public void addAllInitialStates(Collection<GS> init) {
    unvisited_states.clear();
    for (GS state : init) {
      addInitialState(state);
    }
  }

  /**
   * Add `init` in the set of initial states to visit.
   * If the given state is not safe, the verification will necessarily fail when
   * visiting it.
   * If the given state is not physical, it will not modify the behavior of the
   * verification.
   * 
   * @param init
   *          An initial state to visit.
   */
  public void addInitialState(GS init) {
    if (init.isLegal()) {
      unvisited_states.add(init);
    }
  }

  /**
   * Launch the proof trying to separate the proofs.
   * 
   * {@see ModelChecker#verify(GraphSimulatorInterface)}
   * 
   * @throws IOException
   * @throws IllegalAccessException
   * @throws InstantiationException
   * 
   */
  public GS verify(GraphSimulatorInterface<GS, M, S, T> simulator)
      throws IOException, InstantiationException, IllegalAccessException {

    BuildActivationGraph<M, S, T> split_engine = new BuildActivationGraph<>(
        simulator);

    split_engine.printToImage("tmp");
    ProofBySpliting<GS, M, S, T> split_proof = new ProofBySpliting<>(simulator
        .getModel(),
        simulator.getProof());
    for (GraphSimulatorInterface<GS, M, S, T> sub_simulator : split_proof
        .getSimulators()) {
      model_checker.reset();
      model_checker.addAllInitialStates(unvisited_states);
      System.out.print("Proof with those graphs : \n");
      Iterator<M> machine_iterator = sub_simulator.getProof().iterator();
      while (machine_iterator.hasNext()) {
        System.out.print(machine_iterator.next().getName() + "\n");
      }
      machine_iterator = sub_simulator.getModel().iterator();
      while (machine_iterator.hasNext()) {
        System.out.print(machine_iterator.next().getName() + "\n");
      }
      GS final_state = model_checker.verify(sub_simulator);
      if (final_state == null) {
        System.out.print("Proof SUCCESS \n");
      } else {
        System.out.print("Proof FAIL \n");
      }
    }
    return null;
  }
}

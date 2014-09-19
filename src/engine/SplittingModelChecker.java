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
import engine.traceTree.ModelCheckerDisplayer;

public class SplittingModelChecker<GS extends AbstractGlobalState<M, S, T, ?>, M extends AbstractStateMachine<S, T>, S extends AbstractState<T>, T extends AbstractTransition<S>>
    implements ModelCheckerInterface<GS, M, S, T> {

  /** Display the execution trace. It implies SPLIT_PROOF = false */
  private static final boolean DISPLAY_TREE = false;

  private ModelChecker<GS, M, S, T> model_checker;

  /** The states to explore */
  protected Set<GS> unvisited_states = new HashSet<GS>();

  public SplittingModelChecker() {
    super();
    if (DISPLAY_TREE) {
      model_checker = new ModelCheckerDisplayer<>();
    } else {
      model_checker = new ModelChecker<>();
    }
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
    boolean successproof = true;
    split_engine.printToImage("tmp");

    ProofBySpliting<GS, M, S, T> split_proof = new ProofBySpliting<>(simulator
        .getModel(),
        simulator.getProof());
    int i = 0;
    for (GraphSimulatorInterface<GS, M, S, T> sub_simulator : split_proof
        .getSimulators()) {
      model_checker.reset();
      model_checker.addAllInitialStates(unvisited_states);
      System.out.flush();
      System.err.flush();
      System.out.print("Proof with those graphs : \n");
      System.out.flush();
      System.err.flush();
      i++;
      split_engine.printToImage("tmp_" + i, sub_simulator);

      Iterator<M> machine_iterator = sub_simulator.getProof().iterator();
      while (machine_iterator.hasNext()) {
        System.out.flush();
        System.err.flush();
        System.out.print(machine_iterator.next().getName() + "\n");
      }
      machine_iterator = sub_simulator.getModel().iterator();
      while (machine_iterator.hasNext()) {
        System.out.flush();
        System.err.flush();
        System.out.print(machine_iterator.next().getName() + "\n");
        System.out.flush();
        System.err.flush();
      }
      System.out.flush();
      System.err.flush();
      GS final_state = model_checker.verify(sub_simulator);
      System.err.flush();
      System.out.flush();
      if (final_state == null) {
        System.out.print("Proof SUCCESS \n");
      } else {
        System.out.print("Proof FAIL \n");
        successproof = false;
      }
      System.out.flush();
      System.err.flush();
    }
    if (!successproof) {
      System.out.print("**** One of the proofs failed ****");
    }
    return null;
  }
}

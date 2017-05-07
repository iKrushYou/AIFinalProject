# AIFinalProject
4x4 Tic Tac Toe final project for Artificial Intelligence (CS4613)

The game is played between a human and a computer. The computer has two different settings that can be modified to change the difficulty.
Cutoff Time - the max amount of compute time allowed to calculate a move
Cutoff Depth - the maximum depth the search tree can go before determining a move

Easy:
  - Computer makes a random move every turn

Medium:
  - The computer plays with hard difficulty settings, but there is a 30% chance that it plays with a random move instead.

Hard:
  - time cutoff: 10000 ms
  - depth cutoff: 0 (there is no cutoff)
  - Uses the minimax algorithm with A-B pruning. At cutoff, the evaluation function is 6 * X3 + 3 * X2 + X1 - (6 * O3 + 3 * O2 + O1) where X3 is the number of rows / columns / diags with 3 Xs and 1 blank space, O2 is the number of rows / columns / diags with 2 Os and 2 blank spaces…etc.

If you would like the computer to go first, you may do so at the beginning of any new game.

On the right column, there are a list of stats:
  - Total Time (total time to calculate move)
  - Cutoff Occurred (did the algorithm finish or did it get cut off by time / depth constraints)
  - Depth Reached (what’s the max tree depth searched)
  - Nodes Explored (how many nodes were explored total)
  - Max Value Pruning
  - Min Value Pruning
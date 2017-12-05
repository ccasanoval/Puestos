#include <jni.h>
#include <string>
#include <sstream>
#include <iostream>
#include <stdio.h>


////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// STL A* Search implementation
// (C)2001 Justin Heyes-Jones
//
// Finding a path on a simple grid maze
// This shows how to do shortest path finding using A*
////////////////////////////////////////////////////////////////////////////////////////////////////////////////

#include "stlastar.h" // See header for copyright and usage information

using namespace std;

//TODO: pasarlo como parametro a la funcion...
const int MAP_WIDTH = 20;
const int MAP_HEIGHT = 20;
int world_map[ MAP_WIDTH * MAP_HEIGHT ] =
{
// 0001020304050607080910111213141516171819
    1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,   // 00
    1,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,1,   // 01
    1,9,9,1,1,9,9,9,1,9,1,9,1,9,1,9,9,9,1,1,   // 02
    1,9,9,1,1,9,9,9,1,9,1,9,1,9,1,9,9,9,1,1,   // 03
    1,9,1,1,1,1,9,9,1,9,1,9,1,1,1,1,9,9,1,1,   // 04
    1,9,1,1,9,1,1,1,1,9,1,1,1,1,9,1,1,1,1,1,   // 05
    1,9,9,9,9,1,1,1,1,1,1,9,9,9,9,1,1,1,1,1,   // 06
    1,9,9,9,9,9,9,9,9,1,1,1,9,9,9,9,9,9,9,1,   // 07
    1,9,1,1,1,1,1,1,1,1,1,9,1,1,1,1,1,1,1,1,   // 08
    1,9,1,9,9,9,9,9,9,9,1,1,9,9,9,9,9,9,9,1,   // 09
    1,9,1,1,1,1,9,1,1,9,1,1,1,1,1,1,1,1,1,1,   // 10
    1,9,9,9,9,9,1,9,1,9,1,9,9,9,9,9,1,1,1,1,   // 11
    1,9,1,9,1,9,9,9,1,9,1,9,1,9,1,9,9,9,1,1,   // 12
    1,9,1,9,1,9,9,9,1,9,1,9,1,9,1,9,9,9,1,1,   // 13
    1,9,1,1,1,1,9,9,1,9,1,9,1,1,1,1,9,9,1,1,   // 14
    1,9,1,1,9,1,1,1,1,9,1,1,1,1,9,1,1,1,1,1,   // 15
    1,9,9,9,9,1,1,1,1,1,1,9,9,9,9,1,1,1,1,1,   // 16
    1,1,9,9,9,9,9,9,9,1,1,1,9,9,9,1,9,9,9,9,   // 17
    1,9,1,1,1,1,1,1,1,1,1,9,1,1,1,1,1,1,1,1,   // 18
    1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,   // 19
};

// map helper functions

int GetMap( int x, int y )
{
    if( x < 0 ||
        x >= MAP_WIDTH ||
        y < 0 ||
        y >= MAP_HEIGHT
            )
    {
        return 9;
    }

    return world_map[(y*MAP_WIDTH)+x];
}

////////////////////////////////////////////////////////////////////////////////////////////////////
class MapSearchNode
{
public:
    int x;	 // the (x,y) positions of the node
    int y;

    MapSearchNode() { x = y = 0; }
    MapSearchNode( int px, int py ) { x=px; y=py; }

    float GoalDistanceEstimate( MapSearchNode &nodeGoal );
    bool IsGoal( MapSearchNode &nodeGoal );
    bool GetSuccessors( AStarSearch<MapSearchNode> *astarsearch, MapSearchNode *parent_node );
    float GetCost( MapSearchNode &successor );
    bool IsSameState( MapSearchNode &rhs );

    void PrintNodeInfo(stringstream *res);
};

bool MapSearchNode::IsSameState( MapSearchNode &rhs )
{
    // same state in a maze search is simply when (x,y) are the same
    if(x == rhs.x && y == rhs.y)
        return true;
    else
        return false;
}

void MapSearchNode::PrintNodeInfo(stringstream *res)
{
    char str[100];
    sprintf( str, "Node position : (%d,%d)\n", x,y );

    (*res) << str;
}

// Here's the heuristic function that estimates the distance from a Node
// to the Goal.

float MapSearchNode::GoalDistanceEstimate( MapSearchNode &nodeGoal )
{
    return abs(x - nodeGoal.x) + abs(y - nodeGoal.y);
}

bool MapSearchNode::IsGoal( MapSearchNode &nodeGoal )
{
    if(x == nodeGoal.x && y == nodeGoal.y)
        return true;
    return false;
}

// This generates the successors to the given Node. It uses a helper function called
// AddSuccessor to give the successors to the AStar class. The A* specific initialisation
// is done for each node internally, so here you just set the state information that
// is specific to the application
bool MapSearchNode::GetSuccessors( AStarSearch<MapSearchNode> *astarsearch, MapSearchNode *parent_node )
{
    int parent_x = -1;
    int parent_y = -1;

    if( parent_node )
    {
        parent_x = parent_node->x;
        parent_y = parent_node->y;
    }

    MapSearchNode NewNode;

    // push each possible move except allowing the search to go backwards

    if( (GetMap( x-1, y ) < 9)
        && !((parent_x == x-1) && (parent_y == y))
            )
    {
        NewNode = MapSearchNode( x-1, y );
        astarsearch->AddSuccessor( NewNode );
    }

    if( (GetMap( x, y-1 ) < 9)
        && !((parent_x == x) && (parent_y == y-1))
            )
    {
        NewNode = MapSearchNode( x, y-1 );
        astarsearch->AddSuccessor( NewNode );
    }

    if( (GetMap( x+1, y ) < 9)
        && !((parent_x == x+1) && (parent_y == y))
            )
    {
        NewNode = MapSearchNode( x+1, y );
        astarsearch->AddSuccessor( NewNode );
    }


    if( (GetMap( x, y+1 ) < 9)
        && !((parent_x == x) && (parent_y == y+1))
            )
    {
        NewNode = MapSearchNode( x, y+1 );
        astarsearch->AddSuccessor( NewNode );
    }

    return true;
}

// given this node, what does it cost to move to successor. In the case of our map
// the answer is the map terrain value at this node since that is conceptually where we're moving
float MapSearchNode::GetCost( MapSearchNode &successor )
{
    return (float) GetMap( x, y );
}

//__________________________________________________________________________________________________
// CALC MAPA
//__________________________________________________________________________________________________
extern "C"
JNIEXPORT jstring
JNICALL
Java_com_cesoft_puestos_util_Astar_calcMapa(JNIEnv *env, jobject)
{
    //TODO: INPUT: array mapa, pos ini, pos end
    stringstream res;
    res << "INI:\n";

    //----------------------------------------------------------------------------------------------
    AStarSearch<MapSearchNode> astarsearch;

    unsigned int SearchCount = 0;

    const unsigned int NumSearches = 1;

    while(SearchCount < NumSearches)
    {

        // Create a start state
        MapSearchNode nodeStart;
        nodeStart.x = 0;//rand()%MAP_WIDTH;
        nodeStart.y = 0;//rand()%MAP_HEIGHT;

        // Define the goal state
        MapSearchNode nodeEnd;
        nodeEnd.x = 7;//rand()%MAP_WIDTH;
        nodeEnd.y = 15;//rand()%MAP_HEIGHT;

        // Set Start and goal states

        astarsearch.SetStartAndGoalStates( nodeStart, nodeEnd );

        unsigned int SearchState;
        unsigned int SearchSteps = 0;

        do
        {
            SearchState = astarsearch.SearchStep();

            SearchSteps++;
        }
        while( SearchState == AStarSearch<MapSearchNode>::SEARCH_STATE_SEARCHING );

        if( SearchState == AStarSearch<MapSearchNode>::SEARCH_STATE_SUCCEEDED )
        {
            res << "Search found goal state\n";

            MapSearchNode *node = astarsearch.GetSolutionStart();

            int steps = 0;

            node->PrintNodeInfo(&res);
            for( ;; )
            {
                node = astarsearch.GetSolutionNext();

                if( !node )
                    break;

                node->PrintNodeInfo(&res);
                steps ++;
            };

            res << "Solution steps " << steps << endl;

            // Once you're done with the solution you can free the nodes up
            astarsearch.FreeSolutionNodes();


        }
        else if( SearchState == AStarSearch<MapSearchNode>::SEARCH_STATE_FAILED )
        {
            res << "Search terminated. Did not find goal state\n";

        }

        // Display the number of loops the search went through
        res << "SearchSteps : " << SearchSteps << "\n";

        SearchCount ++;
        astarsearch.EnsureMemoryFreed();
    }

    //----------------------------------------------------------------------------------------------
    res << "END\n";

    std::string s = res.str();
    return env->NewStringUTF(s.c_str());
}

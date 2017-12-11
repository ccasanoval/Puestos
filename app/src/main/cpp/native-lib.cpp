#include <jni.h>
#include <string>
#include <sstream>
#include <iostream>
//#include <stdio.h>

/* SOLUCION
{
    "solucion": {
        "resultado": "ok",
        "pasos": "999",
        "pasoBusqueda": "999",
        "camino": [
            { col, row },
            { col, row } ...
        ]
    }
}
*/

////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// STL A* Search implementation (C)2001 Justin Heyes-Jones
// Modified by Cesar Casanova, 2017
////////////////////////////////////////////////////////////////////////////////////////////////////////////////

#include "stlastar.h"
using namespace std;


////////////////////////////////////////////////////////////////////////////////////////////////////
jbyte* MAP;
int COLS;
int ROWS;
int GetMap( int x, int y)
{
    if( x < 0 || x >= COLS || y < 0 || y >= ROWS)
        return 9;
    return MAP[(y*COLS)+x];
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
    return x == rhs.x && y == rhs.y;
}

void MapSearchNode::PrintNodeInfo(stringstream *res)
{
    (*res) << "[\"" << x << "\", \"" << y << "\"]" << endl;
}

// Here's the heuristic function that estimates the distance from a Node to the Goal.
float MapSearchNode::GoalDistanceEstimate( MapSearchNode &nodeGoal )
{
    return abs(x - nodeGoal.x) + abs(y - nodeGoal.y);
}

bool MapSearchNode::IsGoal( MapSearchNode &nodeGoal )
{
    return x == nodeGoal.x && y == nodeGoal.y;
}

// This generates the successors to the given Node. It uses a helper function called AddSuccessor
// to give the successors to the AStar class. The A* specific initialisation is done for each node
// internally, so here you just set the state information that is specific to the application
bool MapSearchNode::GetSuccessors(
        AStarSearch<MapSearchNode> *astarsearch,
        MapSearchNode *parent_node)
{
    int parent_x = -1;
    int parent_y = -1;

    if(parent_node)
    {
        parent_x = parent_node->x;
        parent_y = parent_node->y;
    }

    MapSearchNode NewNode;
    // push each possible move except allowing the search to go backwards
    if( (GetMap( x-1, y ) < 9) && !((parent_x == x-1) && (parent_y == y)) )
    {
        NewNode = MapSearchNode( x-1, y );
        astarsearch->AddSuccessor( NewNode );
    }
    if( (GetMap( x, y-1 ) < 9) && !((parent_x == x) && (parent_y == y-1)) )
    {
        NewNode = MapSearchNode( x, y-1 );
        astarsearch->AddSuccessor( NewNode );
    }
    if( (GetMap( x+1, y ) < 9) && !((parent_x == x+1) && (parent_y == y)) )
    {
        NewNode = MapSearchNode( x+1, y );
        astarsearch->AddSuccessor( NewNode );
    }
    if( (GetMap( x, y+1 ) < 9) && !((parent_x == x) && (parent_y == y+1)) )
    {
        NewNode = MapSearchNode( x, y+1 );
        astarsearch->AddSuccessor( NewNode );
    }

    //TEST CES: Movimientos Diagonales
    if( (GetMap( x-1, y-1 ) < 9) && !((parent_x == x-1) && (parent_y == y-1)) )
    {
        NewNode = MapSearchNode( x-1, y-1 );
        astarsearch->AddSuccessor( NewNode );
    }
    if( (GetMap( x+1, y-1 ) < 9) && !((parent_x == x+1) && (parent_y == y-1)) )
    {
        NewNode = MapSearchNode( x+1, y-1 );
        astarsearch->AddSuccessor( NewNode );
    }
    if( (GetMap( x+1, y+1 ) < 9) && !((parent_x == x+1) && (parent_y == y+1)) )
    {
        NewNode = MapSearchNode( x+1, y+1 );
        astarsearch->AddSuccessor( NewNode );
    }
    if( (GetMap( x-1, y+1 ) < 9) && !((parent_x == x-1) && (parent_y == y+1)) )
    {
        NewNode = MapSearchNode( x-1, y+1 );
        astarsearch->AddSuccessor( NewNode );
    }

    return true;
}

// given this node, what does it cost to move to successor. In the case of our map
// the answer is the map terrain value at this node since that is conceptually where we're moving
float MapSearchNode::GetCost( MapSearchNode &successor )
{
    //Tarda demasiado
    //float distancia = (successor.x - x)*(successor.x - x) + (successor.y - y)*(successor.y - y);
    //return (float) GetMap( x, y ) + distancia;
    float distancia = GetMap( x, y );//1;
    if(successor.x != x && successor.y != y) distancia+=0.5f;
    return distancia;
}

//__________________________________________________________________________________________________
// CALC MAPA
//__________________________________________________________________________________________________
extern "C"
JNIEXPORT jstring
JNICALL
Java_com_cesoft_puestos_util_Astar_calcMapa(JNIEnv *env, jobject,
                                            int iniX, int iniY,
                                            int endX, int endY,
                                            jbyteArray map,
                                            int cols, int rows)
{
    stringstream res;
    //res << "{ \"solucion\": { \"resultado\": ";
    res << " { \"resultado\": ";

    // ---- MAPA
    MAP = env->GetByteArrayElements(map, NULL);
    COLS = cols;
    ROWS = rows;

    //----------------------------------------------------------------------------------------------
    AStarSearch<MapSearchNode> astarsearch;

    unsigned int SearchCount = 0;
    const unsigned int NumSearches = 1;//?
    while(SearchCount < NumSearches)
    {
        // Create a start state
        MapSearchNode nodeStart;
        nodeStart.x = iniX;
        nodeStart.y = iniY;

        // Define the goal state
        MapSearchNode nodeEnd;
        nodeEnd.x = endX;
        nodeEnd.y = endY;

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
            res << " \"ok\", \"camino\": [ " << endl;
            MapSearchNode *node = astarsearch.GetSolutionStart();
            int steps = 0;
            node->PrintNodeInfo(&res);
            for( ;; )
            {
                node = astarsearch.GetSolutionNext();
                if( !node )
                    break;
                res << ",";
                node->PrintNodeInfo(&res);
                steps ++;
            };
            res << " ], \"pasos\":\"" << steps << "\", \"pasosBusqueda\": ";

            // Once you're done with the solution you can free the nodes up
            astarsearch.FreeSolutionNodes();
        }
        else if( SearchState == AStarSearch<MapSearchNode>::SEARCH_STATE_FAILED )
        {
            res << "\"ko\", \"camino\":[], \"pasos\":\"0\", \"pasosBusqueda\": ";
        }

        // Display the number of loops the search went through
        res << "\"" << SearchSteps << "\" } " << endl;

        SearchCount ++;
        astarsearch.EnsureMemoryFreed();
    }

    //----------------------------------------------------------------------------------------------
    env->ReleaseByteArrayElements(map, MAP, 0);
    MAP = NULL;

    std::string s = res.str();
    return env->NewStringUTF(s.c_str());
}

/*
//__________________________________________________________________________________________________
extern "C"
JNIEXPORT jstring
JNICALL
Java_com_cesoft_puestos_util_Astar_calcMapa(JNIEnv *env, jobject o,
                                            int iniX, int iniY,
                                            int endX, int endY,
                                            jbyteArray map,
                                            int cols, int rows)
{
    return Java_com_cesoft_puestos_util_AStar_calcMapa(
            env, o, iniX, iniY,
            endX, endY,
            map,
            cols, rows);
}*/
#include <jni.h>
#include <string>
#include <sstream>
#include <iostream>
//#include <stdio.h>


////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// STL A* Search implementation
// (C)2001 Justin Heyes-Jones
//
// Finding a path on a simple grid maze
// This shows how to do shortest path finding using A*
////////////////////////////////////////////////////////////////////////////////////////////////////////////////

#include "stlastar.h" // See header for copyright and usage information

using namespace std;

/*const int MAP_WIDTH = 68;
const int MAP_HEIGHT = 41;
jbyte world_map[ MAP_WIDTH * MAP_HEIGHT ] =
{
//  0 1 2 3 4 5 6 7 8 910 1 2 3 4 5 6 7 8 920 1 2 3 4 5 6 7 8 930 1 2 3 4 5 6 7 8 940 1 2 3 4 5 6 7 8 950 1 2 3 4 5 6 7 8 960 1 2 3 4 5 6 7
    1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,//0
    1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,//1
    1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,//2
    1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,1,9,9,9,9,9,9,9,9,9,9,9,1,1,1,//3
    1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,9,1,1,9,9,9,9,9,9,9,9,1,1,1,1,1,9,1,1,9,9,1,1,9,1,1,9,1,1,1,//4
    1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,9,1,1,9,9,9,9,9,9,9,9,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,9,1,1,1,//5
    1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,9,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,9,9,9,9,1,9,1,1,1,//6
    1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,9,1,1,9,1,1,9,1,1,1,1,9,9,9,9,9,9,1,9,1,1,9,9,9,9,1,9,1,1,1,//7
    1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,9,1,1,1,1,1,1,1,1,1,9,9,1,1,1,1,9,9,1,1,1,1,1,1,1,1,9,1,1,1,//8
    1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,9,1,1,9,9,9,9,1,1,1,9,9,1,1,1,1,9,1,1,1,1,1,1,1,9,1,9,1,1,1,//9
    1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,9,1,1,1,1,1,1,1,1,1,9,9,1,1,1,1,9,9,1,1,1,1,1,1,1,1,9,1,1,1,//10
    1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,9,1,1,9,1,1,9,1,1,1,9,9,1,1,1,1,9,9,1,1,9,9,9,9,9,1,9,1,1,1,//11
    1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,9,1,1,1,1,1,1,1,1,1,9,9,1,1,1,1,9,1,1,1,1,1,1,1,1,1,9,1,1,1,//12
    1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,9,1,1,9,9,9,9,1,1,1,9,9,1,1,1,1,9,9,1,1,1,1,1,1,1,1,9,1,1,1,//13
    1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,9,1,1,1,1,1,1,1,1,1,9,9,1,1,1,1,9,9,9,1,1,1,1,1,9,1,9,1,1,1,//14
    1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,9,9,9,9,9,9,9,1,1,1,9,9,9,9,9,9,9,9,1,1,1,9,9,9,9,1,9,1,1,1,//15
    1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,9,1,1,1,1,1,9,1,1,1,1,9,9,9,9,1,1,9,1,1,1,1,9,9,1,1,9,1,1,1,//16
    1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,9,1,1,1,1,1,9,1,1,1,1,9,1,1,9,1,1,9,1,1,1,1,1,1,1,1,9,1,1,1,//17
    1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,9,1,1,9,1,1,1,1,1,1,1,9,1,1,9,1,1,9,1,1,1,1,1,1,1,1,9,1,1,1,//18
    1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,9,1,1,1,9,9,9,1,1,9,1,1,1,1,1,1,1,1,1,1,1,9,9,9,9,1,9,1,1,1,//19
    1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,9,1,1,1,9,9,9,1,1,9,1,1,1,1,1,1,1,1,1,1,1,1,9,9,9,1,9,1,1,1,//20
    1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,9,9,9,9,1,1,1,1,1,1,1,9,9,9,9,9,9,9,1,1,1,1,1,1,1,1,9,1,1,1,//21
    1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,9,1,1,9,9,9,9,1,1,1,1,9,1,1,1,1,9,9,1,1,1,1,1,1,1,1,9,1,1,1,//22
    1,1,1,1,1,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,1,1,1,1,9,9,1,9,9,1,1,1,9,1,1,1,1,9,9,1,1,9,1,1,1,1,1,9,1,1,1,//23
    1,1,1,1,1,9,1,1,1,1,1,1,1,1,1,1,1,1,9,1,1,1,1,1,9,1,1,1,1,9,1,1,1,1,9,1,1,1,1,1,1,1,9,9,1,9,9,1,1,9,9,9,9,9,9,9,1,1,9,9,9,9,9,1,9,1,1,1,//24
    1,1,1,1,1,9,1,1,1,1,1,1,9,1,1,1,1,1,9,1,1,1,1,1,9,1,1,1,1,1,1,9,9,9,9,9,1,1,1,1,1,1,1,9,9,9,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,9,1,1,1,//25
    1,1,1,1,1,9,1,1,1,9,9,1,1,1,1,1,9,1,1,9,9,9,9,1,1,9,9,9,9,9,1,9,1,1,1,9,9,9,9,9,9,1,1,1,1,1,1,9,1,1,1,9,1,1,1,1,1,1,1,1,1,1,1,1,9,1,1,1,//26
    1,1,1,1,1,9,1,1,1,1,1,1,9,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,9,1,1,1,1,9,9,9,9,9,9,9,1,1,1,9,9,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,9,1,1,1,//27
    1,1,1,1,1,9,1,1,1,1,1,1,9,1,1,1,1,1,1,9,1,1,1,1,1,1,1,1,1,1,1,9,9,1,1,1,9,9,9,1,1,1,1,1,1,9,9,9,1,1,1,9,9,1,1,9,9,1,1,9,9,9,9,1,9,1,1,1,//28
    1,1,1,1,1,9,1,1,1,1,1,1,1,1,1,1,1,1,1,9,1,1,1,1,1,1,1,1,1,1,1,9,9,1,1,1,1,1,1,1,1,1,1,1,9,9,9,1,1,1,1,9,9,1,1,9,9,1,1,9,9,9,9,1,9,1,1,1,//29
    1,1,1,1,1,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,1,1,1,1,1,1,1,1,1,9,9,9,9,9,9,1,1,1,1,1,1,1,1,1,9,9,9,9,9,9,9,1,1,1,1,1,1,1,1,9,1,1,1,//30
    1,1,1,1,1,9,1,1,1,9,1,1,9,1,1,1,1,1,1,1,1,1,1,1,9,9,9,9,1,1,1,1,1,1,9,9,1,1,1,9,1,1,1,1,1,1,9,9,9,9,1,1,1,1,9,9,1,1,1,1,1,1,1,1,9,1,1,1,//31
    1,1,1,1,1,9,1,1,9,9,1,1,9,1,1,1,1,1,1,1,1,1,1,1,9,1,1,1,1,1,1,1,1,1,9,9,1,1,1,9,1,1,1,1,1,1,1,1,1,9,1,1,1,1,9,9,1,1,1,1,9,1,9,1,9,1,1,1,//32
    1,1,1,1,1,1,1,1,1,1,1,1,9,9,9,9,9,9,9,9,9,9,9,9,9,9,1,1,9,1,1,9,1,1,9,9,9,9,9,9,1,1,9,1,1,1,1,9,9,9,9,9,9,9,9,9,1,1,1,9,9,9,9,1,9,1,1,1,//33
    1,1,1,1,1,9,1,1,1,1,1,1,9,1,9,1,1,1,9,1,1,9,1,1,9,1,1,1,9,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,9,9,1,1,9,1,1,9,9,9,1,1,1,1,1,1,1,1,9,1,1,1,//34
    1,1,1,1,1,9,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,9,1,1,1,//35
    1,1,1,1,1,9,1,1,1,9,1,1,9,1,1,9,1,1,9,1,1,9,1,1,1,1,1,1,9,1,1,1,1,1,1,9,1,1,1,1,1,1,9,1,1,1,1,1,1,9,1,1,1,1,1,1,9,1,1,1,1,1,1,1,9,1,1,1,//36
    1,1,1,1,1,9,9,1,1,9,1,1,9,1,1,9,1,1,9,1,1,9,1,1,1,1,1,1,9,1,1,1,1,1,1,9,1,1,1,1,1,1,9,1,1,1,1,1,1,9,1,1,1,1,1,1,9,9,9,9,1,9,1,1,9,1,1,1,//37
    1,1,1,1,1,9,1,1,1,9,1,1,9,1,1,9,1,1,9,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,9,1,1,1,9,1,1,9,1,1,1,//38
    1,1,1,1,1,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,1,1,1,//39
    1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,//40
};*/
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
    //char str[100];
    //sprintf( str, "(%d,%d)\n", x,y );
    (*res) << "(" << x << "," << y << ")" << endl;//str;
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

    if( parent_node )
    {
        parent_x = parent_node->x;
        parent_y = parent_node->y;
    }

    MapSearchNode NewNode;

    // push each possible move except allowing the search to go backwards
    if( (GetMap( x-1, y ) < 9)
        && !((parent_x == x-1) && (parent_y == y)) )
    {
        NewNode = MapSearchNode( x-1, y );
        astarsearch->AddSuccessor( NewNode );
    }

    if( (GetMap( x, y-1 ) < 9)
        && !((parent_x == x) && (parent_y == y-1)) )
    {
        NewNode = MapSearchNode( x, y-1 );
        astarsearch->AddSuccessor( NewNode );
    }

    if( (GetMap( x+1, y ) < 9)
        && !((parent_x == x+1) && (parent_y == y)) )
    {
        NewNode = MapSearchNode( x+1, y );
        astarsearch->AddSuccessor( NewNode );
    }

    if( (GetMap( x, y+1 ) < 9)
        && !((parent_x == x) && (parent_y == y+1)) )
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
Java_com_cesoft_puestos_util_AStar_calcMapa(JNIEnv *env, jobject,
                                            int iniX, int iniY,
                                            int endX, int endY,
                                            jbyteArray map,
                                            int cols, int rows)
{
    //TODO: INPUT: array mapa, pos ini, pos end
    stringstream res;
    res << "INI:\n";

    // ---- MAPA
    //const jsize length = env->GetArrayLength(map);
    //jbyteArray newArray = env->NewByteArray(length);
    //jbyte* map2 = env->GetByteArrayElements(map, NULL);
    MAP = env->GetByteArrayElements(map, NULL);

    //
    COLS = cols;
    ROWS = rows;

    /*res << COLS << endl << ROWS << endl;
    for(int i=0; i < ROWS; i++) {
        for(int j = 0; j < COLS; j++) {
            res << MAP[i * COLS + j] << ",";
        }
        res << endl;
    }
    res << endl;
    res << endl;
    res << MAP_WIDTH << endl << MAP_HEIGHT << endl;
    for(int i=0; i < MAP_HEIGHT; i++) {
        for(int j = 0; j < MAP_WIDTH; j++) {
            res << world_map[i * MAP_WIDTH + j] << ",";
        }
        res << endl;
    }*/
    //std::string s2 = res.str();
    //return env->NewStringUTF(s2.c_str());

    //----------------------------------------------------------------------------------------------
    AStarSearch<MapSearchNode> astarsearch;

    unsigned int SearchCount = 0;
    const unsigned int NumSearches = 1;//?
    while(SearchCount < NumSearches)
    {
        // Create a start state
        MapSearchNode nodeStart;
        nodeStart.x = iniX;//rand()%MAP_WIDTH;
        nodeStart.y = iniY;//rand()%MAP_HEIGHT;

        // Define the goal state
        MapSearchNode nodeEnd;
        nodeEnd.x = endX;//rand()%MAP_WIDTH;
        nodeEnd.y = endY;//rand()%MAP_HEIGHT;

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
            res << "OK\n";
            MapSearchNode *node = astarsearch.GetSolutionStart();
            int steps = 0;
            node->PrintNodeInfo(&res);
            //res << "(" << (*node).x << "," << (*node).y << ")" << endl;
            for( ;; )
            {
                node = astarsearch.GetSolutionNext();
                if( !node )
                    break;
                node->PrintNodeInfo(&res);
                steps ++;
            };
            res << "SOLUTION:" << steps << endl;

            // Once you're done with the solution you can free the nodes up
            astarsearch.FreeSolutionNodes();
        }
        else if( SearchState == AStarSearch<MapSearchNode>::SEARCH_STATE_FAILED )
        {
            res << "KO\n";
        }

        // Display the number of loops the search went through
        res << "SEARCH:" << SearchSteps << endl;

        SearchCount ++;
        astarsearch.EnsureMemoryFreed();
    }

    //----------------------------------------------------------------------------------------------
    env->ReleaseByteArrayElements(map, MAP, 0);
    MAP = NULL;
    res << "END\n";

    std::string s = res.str();
    return env->NewStringUTF(s.c_str());
}

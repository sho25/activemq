begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activecluster
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|JMSException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activecluster
operator|.
name|Cluster
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activecluster
operator|.
name|ClusterEvent
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activecluster
operator|.
name|ClusterException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activecluster
operator|.
name|ClusterFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activecluster
operator|.
name|ClusterListener
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activecluster
operator|.
name|impl
operator|.
name|ActiveMQClusterFactory
import|;
end_import

begin_comment
comment|/**  * @version $Revision: 1.2 $  */
end_comment

begin_class
specifier|public
class|class
name|ChatDemo
implements|implements
name|ClusterListener
block|{
specifier|private
name|Cluster
name|cluster
decl_stmt|;
specifier|private
name|String
name|name
init|=
literal|"unknown"
decl_stmt|;
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
try|try
block|{
name|ChatDemo
name|test
init|=
operator|new
name|ChatDemo
argument_list|()
decl_stmt|;
name|test
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Caught: "
operator|+
name|e
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|Exception
name|c
init|=
name|e
operator|.
name|getLinkedException
argument_list|()
decl_stmt|;
if|if
condition|(
name|c
operator|!=
literal|null
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Cause: "
operator|+
name|c
argument_list|)
expr_stmt|;
name|c
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Caught: "
operator|+
name|e
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|run
parameter_list|()
throws|throws
name|Exception
block|{
name|cluster
operator|=
name|createCluster
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|addClusterListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|start
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Welcome to the ActiveCluster Chat Demo!"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Enter text to talk or type"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  /quit      to terminate the application"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  /name foo  to change your name to be 'foo'"
argument_list|)
expr_stmt|;
name|BufferedReader
name|reader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|System
operator|.
name|in
argument_list|)
argument_list|)
decl_stmt|;
name|boolean
name|running
init|=
literal|true
decl_stmt|;
while|while
condition|(
name|running
condition|)
block|{
name|String
name|line
init|=
name|reader
operator|.
name|readLine
argument_list|()
decl_stmt|;
if|if
condition|(
name|line
operator|==
literal|null
operator|||
name|line
operator|.
name|trim
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"quit"
argument_list|)
condition|)
block|{
break|break;
block|}
else|else
block|{
name|running
operator|=
name|processCommand
argument_list|(
name|line
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|stop
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|boolean
name|processCommand
parameter_list|(
name|String
name|text
parameter_list|)
throws|throws
name|JMSException
block|{
if|if
condition|(
name|text
operator|.
name|equals
argument_list|(
literal|"/quit"
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
if|if
condition|(
name|text
operator|.
name|startsWith
argument_list|(
literal|"/name"
argument_list|)
condition|)
block|{
name|name
operator|=
name|text
operator|.
name|substring
argument_list|(
literal|5
argument_list|)
operator|.
name|trim
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"* name now changed to: "
operator|+
name|name
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// lets talk
name|Map
name|map
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"text"
argument_list|,
name|text
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"name"
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|getLocalNode
argument_list|()
operator|.
name|setState
argument_list|(
name|map
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
block|}
specifier|public
name|void
name|onNodeAdd
parameter_list|(
name|ClusterEvent
name|event
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"* "
operator|+
name|getName
argument_list|(
name|event
argument_list|)
operator|+
literal|" has joined the room"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|onNodeUpdate
parameter_list|(
name|ClusterEvent
name|event
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|getName
argument_list|(
name|event
argument_list|)
operator|+
literal|"> "
operator|+
name|getText
argument_list|(
name|event
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|onNodeRemoved
parameter_list|(
name|ClusterEvent
name|event
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"* "
operator|+
name|getName
argument_list|(
name|event
argument_list|)
operator|+
literal|" has left the room"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|onNodeFailed
parameter_list|(
name|ClusterEvent
name|event
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"* "
operator|+
name|getName
argument_list|(
name|event
argument_list|)
operator|+
literal|" has failed unexpectedly"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|onCoordinatorChanged
parameter_list|(
name|ClusterEvent
name|event
parameter_list|)
block|{              }
specifier|protected
name|Object
name|getName
parameter_list|(
name|ClusterEvent
name|event
parameter_list|)
block|{
return|return
name|event
operator|.
name|getNode
argument_list|()
operator|.
name|getState
argument_list|()
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
return|;
block|}
specifier|protected
name|Object
name|getText
parameter_list|(
name|ClusterEvent
name|event
parameter_list|)
block|{
return|return
name|event
operator|.
name|getNode
argument_list|()
operator|.
name|getState
argument_list|()
operator|.
name|get
argument_list|(
literal|"text"
argument_list|)
return|;
block|}
specifier|protected
name|void
name|stop
parameter_list|()
throws|throws
name|JMSException
block|{
name|cluster
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|Cluster
name|createCluster
parameter_list|()
throws|throws
name|JMSException
throws|,
name|ClusterException
block|{
name|ClusterFactory
name|factory
init|=
operator|new
name|ActiveMQClusterFactory
argument_list|()
decl_stmt|;
return|return
name|factory
operator|.
name|createCluster
argument_list|(
literal|"ORG.CODEHAUS.ACTIVEMQ.TEST.CLUSTER"
argument_list|)
return|;
block|}
block|}
end_class

end_unit


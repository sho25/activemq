begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|view
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|Broker
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|ConnectionContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|region
operator|.
name|Destination
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|ActiveMQDestination
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|filter
operator|.
name|DestinationMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|filter
operator|.
name|DestinationMapNode
import|;
end_import

begin_comment
comment|/**  *   */
end_comment

begin_class
specifier|public
class|class
name|DestinationDotFileInterceptor
extends|extends
name|DotFileInterceptorSupport
block|{
specifier|protected
specifier|static
specifier|final
name|String
name|ID_SEPARATOR
init|=
literal|"_"
decl_stmt|;
specifier|public
name|DestinationDotFileInterceptor
parameter_list|(
name|Broker
name|next
parameter_list|,
name|String
name|file
parameter_list|)
block|{
name|super
argument_list|(
name|next
argument_list|,
name|file
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Destination
name|addDestination
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|,
name|boolean
name|create
parameter_list|)
throws|throws
name|Exception
block|{
name|Destination
name|answer
init|=
name|super
operator|.
name|addDestination
argument_list|(
name|context
argument_list|,
name|destination
argument_list|,
name|create
argument_list|)
decl_stmt|;
name|generateFile
argument_list|()
expr_stmt|;
return|return
name|answer
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|removeDestination
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|,
name|long
name|timeout
parameter_list|)
throws|throws
name|Exception
block|{
name|super
operator|.
name|removeDestination
argument_list|(
name|context
argument_list|,
name|destination
argument_list|,
name|timeout
argument_list|)
expr_stmt|;
name|generateFile
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|generateFile
parameter_list|(
name|PrintWriter
name|writer
parameter_list|)
throws|throws
name|Exception
block|{
name|ActiveMQDestination
index|[]
name|destinations
init|=
name|getDestinations
argument_list|()
decl_stmt|;
comment|// lets split into a tree
name|DestinationMap
name|map
init|=
operator|new
name|DestinationMap
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|destinations
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|ActiveMQDestination
name|destination
init|=
name|destinations
index|[
name|i
index|]
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|destination
argument_list|,
name|destination
argument_list|)
expr_stmt|;
block|}
comment|// now lets navigate the tree
name|writer
operator|.
name|println
argument_list|(
literal|"digraph \"ActiveMQ Destinations\" {"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|()
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|"node [style = \"rounded,filled\", fontname=\"Helvetica-Oblique\"];"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|()
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|"topic_root [fillcolor = deepskyblue, label = \"Topics\" ];"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|"queue_root [fillcolor = deepskyblue, label = \"Queues\" ];"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|()
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|"subgraph queues {"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|"  node [fillcolor=red];     "
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|"  label = \"Queues\""
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|()
expr_stmt|;
name|printNodeLinks
argument_list|(
name|writer
argument_list|,
name|map
operator|.
name|getQueueRootNode
argument_list|()
argument_list|,
literal|"queue"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|()
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|"subgraph temp queues {"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|"  node [fillcolor=red];     "
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|"  label = \"TempQueues\""
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|()
expr_stmt|;
name|printNodeLinks
argument_list|(
name|writer
argument_list|,
name|map
operator|.
name|getTempQueueRootNode
argument_list|()
argument_list|,
literal|"tempqueue"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|()
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|"subgraph topics {"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|"  node [fillcolor=green];     "
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|"  label = \"Topics\""
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|()
expr_stmt|;
name|printNodeLinks
argument_list|(
name|writer
argument_list|,
name|map
operator|.
name|getTopicRootNode
argument_list|()
argument_list|,
literal|"topic"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|()
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|"subgraph temp topics {"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|"  node [fillcolor=green];     "
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|"  label = \"TempTopics\""
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|()
expr_stmt|;
name|printNodeLinks
argument_list|(
name|writer
argument_list|,
name|map
operator|.
name|getTempTopicRootNode
argument_list|()
argument_list|,
literal|"temptopic"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|()
expr_stmt|;
name|printNodes
argument_list|(
name|writer
argument_list|,
name|map
operator|.
name|getQueueRootNode
argument_list|()
argument_list|,
literal|"queue"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|()
expr_stmt|;
name|printNodes
argument_list|(
name|writer
argument_list|,
name|map
operator|.
name|getTempQueueRootNode
argument_list|()
argument_list|,
literal|"tempqueue"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|()
expr_stmt|;
name|printNodes
argument_list|(
name|writer
argument_list|,
name|map
operator|.
name|getTopicRootNode
argument_list|()
argument_list|,
literal|"topic"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|()
expr_stmt|;
name|printNodes
argument_list|(
name|writer
argument_list|,
name|map
operator|.
name|getTempTopicRootNode
argument_list|()
argument_list|,
literal|"temptopic"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|()
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|printNodes
parameter_list|(
name|PrintWriter
name|writer
parameter_list|,
name|DestinationMapNode
name|node
parameter_list|,
name|String
name|prefix
parameter_list|)
block|{
name|String
name|path
init|=
name|getPath
argument_list|(
name|node
argument_list|)
decl_stmt|;
name|writer
operator|.
name|print
argument_list|(
literal|"  "
argument_list|)
expr_stmt|;
name|writer
operator|.
name|print
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
name|writer
operator|.
name|print
argument_list|(
name|ID_SEPARATOR
argument_list|)
expr_stmt|;
name|writer
operator|.
name|print
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|String
name|label
init|=
name|path
decl_stmt|;
if|if
condition|(
name|prefix
operator|.
name|equals
argument_list|(
literal|"topic"
argument_list|)
condition|)
block|{
name|label
operator|=
literal|"Topics"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|prefix
operator|.
name|equals
argument_list|(
literal|"queue"
argument_list|)
condition|)
block|{
name|label
operator|=
literal|"Queues"
expr_stmt|;
block|}
name|writer
operator|.
name|print
argument_list|(
literal|"[ label = \""
argument_list|)
expr_stmt|;
name|writer
operator|.
name|print
argument_list|(
name|label
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|"\" ];"
argument_list|)
expr_stmt|;
name|Collection
name|children
init|=
name|node
operator|.
name|getChildren
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
name|iter
init|=
name|children
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|DestinationMapNode
name|child
init|=
operator|(
name|DestinationMapNode
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|printNodes
argument_list|(
name|writer
argument_list|,
name|child
argument_list|,
name|prefix
operator|+
name|ID_SEPARATOR
operator|+
name|path
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|printNodeLinks
parameter_list|(
name|PrintWriter
name|writer
parameter_list|,
name|DestinationMapNode
name|node
parameter_list|,
name|String
name|prefix
parameter_list|)
block|{
name|String
name|path
init|=
name|getPath
argument_list|(
name|node
argument_list|)
decl_stmt|;
name|Collection
name|children
init|=
name|node
operator|.
name|getChildren
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
name|iter
init|=
name|children
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|DestinationMapNode
name|child
init|=
operator|(
name|DestinationMapNode
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|writer
operator|.
name|print
argument_list|(
literal|"  "
argument_list|)
expr_stmt|;
name|writer
operator|.
name|print
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
name|writer
operator|.
name|print
argument_list|(
name|ID_SEPARATOR
argument_list|)
expr_stmt|;
name|writer
operator|.
name|print
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|writer
operator|.
name|print
argument_list|(
literal|" -> "
argument_list|)
expr_stmt|;
name|writer
operator|.
name|print
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
name|writer
operator|.
name|print
argument_list|(
name|ID_SEPARATOR
argument_list|)
expr_stmt|;
name|writer
operator|.
name|print
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|writer
operator|.
name|print
argument_list|(
name|ID_SEPARATOR
argument_list|)
expr_stmt|;
name|writer
operator|.
name|print
argument_list|(
name|getPath
argument_list|(
name|child
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|";"
argument_list|)
expr_stmt|;
name|printNodeLinks
argument_list|(
name|writer
argument_list|,
name|child
argument_list|,
name|prefix
operator|+
name|ID_SEPARATOR
operator|+
name|path
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|String
name|getPath
parameter_list|(
name|DestinationMapNode
name|node
parameter_list|)
block|{
name|String
name|path
init|=
name|node
operator|.
name|getPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|path
operator|.
name|equals
argument_list|(
literal|"*"
argument_list|)
condition|)
block|{
return|return
literal|"root"
return|;
block|}
return|return
name|path
return|;
block|}
block|}
end_class

end_unit


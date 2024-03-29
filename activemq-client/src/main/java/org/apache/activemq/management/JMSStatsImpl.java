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
name|management
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CopyOnWriteArrayList
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
name|ActiveMQConnection
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
name|util
operator|.
name|IndentPrinter
import|;
end_import

begin_comment
comment|/**  * Statistics for a number of JMS connections  *   *   */
end_comment

begin_class
specifier|public
class|class
name|JMSStatsImpl
extends|extends
name|StatsImpl
block|{
specifier|private
name|List
argument_list|<
name|ActiveMQConnection
argument_list|>
name|connections
init|=
operator|new
name|CopyOnWriteArrayList
argument_list|<
name|ActiveMQConnection
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|JMSStatsImpl
parameter_list|()
block|{     }
specifier|public
name|JMSConnectionStatsImpl
index|[]
name|getConnections
parameter_list|()
block|{
name|Object
index|[]
name|connectionArray
init|=
name|connections
operator|.
name|toArray
argument_list|()
decl_stmt|;
name|int
name|size
init|=
name|connectionArray
operator|.
name|length
decl_stmt|;
name|JMSConnectionStatsImpl
index|[]
name|answer
init|=
operator|new
name|JMSConnectionStatsImpl
index|[
name|size
index|]
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|ActiveMQConnection
name|connection
init|=
operator|(
name|ActiveMQConnection
operator|)
name|connectionArray
index|[
name|i
index|]
decl_stmt|;
name|answer
index|[
name|i
index|]
operator|=
name|connection
operator|.
name|getConnectionStats
argument_list|()
expr_stmt|;
block|}
return|return
name|answer
return|;
block|}
specifier|public
name|void
name|addConnection
parameter_list|(
name|ActiveMQConnection
name|connection
parameter_list|)
block|{
name|connections
operator|.
name|add
argument_list|(
name|connection
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|removeConnection
parameter_list|(
name|ActiveMQConnection
name|connection
parameter_list|)
block|{
name|connections
operator|.
name|remove
argument_list|(
name|connection
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|dump
parameter_list|(
name|IndentPrinter
name|out
parameter_list|)
block|{
name|out
operator|.
name|printIndent
argument_list|()
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"factory {"
argument_list|)
expr_stmt|;
name|out
operator|.
name|incrementIndent
argument_list|()
expr_stmt|;
name|JMSConnectionStatsImpl
index|[]
name|array
init|=
name|getConnections
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
name|array
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|JMSConnectionStatsImpl
name|connectionStat
init|=
operator|(
name|JMSConnectionStatsImpl
operator|)
name|array
index|[
name|i
index|]
decl_stmt|;
name|connectionStat
operator|.
name|dump
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|decrementIndent
argument_list|()
expr_stmt|;
name|out
operator|.
name|printIndent
argument_list|()
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
comment|/**      * @param enabled the enabled to set      */
specifier|public
name|void
name|setEnabled
parameter_list|(
name|boolean
name|enabled
parameter_list|)
block|{
name|super
operator|.
name|setEnabled
argument_list|(
name|enabled
argument_list|)
expr_stmt|;
name|JMSConnectionStatsImpl
index|[]
name|stats
init|=
name|getConnections
argument_list|()
decl_stmt|;
name|int
name|size
init|=
name|stats
operator|.
name|length
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|stats
index|[
name|i
index|]
operator|.
name|setEnabled
argument_list|(
name|enabled
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit


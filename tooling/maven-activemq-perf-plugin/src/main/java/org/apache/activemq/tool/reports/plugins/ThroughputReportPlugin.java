begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|tool
operator|.
name|reports
operator|.
name|plugins
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|tool
operator|.
name|reports
operator|.
name|PerformanceStatisticsUtil
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
name|StringTokenizer
import|;
end_import

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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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

begin_class
specifier|public
class|class
name|ThroughputReportPlugin
implements|implements
name|ReportPlugin
block|{
specifier|public
specifier|static
specifier|final
name|String
name|KEY_SYS_TOTAL_TP
init|=
literal|"SystemTotalTP"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|KEY_SYS_TOTAL_CLIENTS
init|=
literal|"SystemTotalClients"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|KEY_SYS_AVE_TP
init|=
literal|"SystemAveTP"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|KEY_SYS_AVE_EMM_TP
init|=
literal|"SystemAveEMMTP"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|KEY_SYS_AVE_CLIENT_TP
init|=
literal|"SystemAveClientTP"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|KEY_SYS_AVE_CLIENT_EMM_TP
init|=
literal|"SystemAveClientEMMTP"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|KEY_MIN_CLIENT_TP
init|=
literal|"MinClientTP"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|KEY_MAX_CLIENT_TP
init|=
literal|"MaxClientTP"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|KEY_MIN_CLIENT_TOTAL_TP
init|=
literal|"MinClientTotalTP"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|KEY_MAX_CLIENT_TOTAL_TP
init|=
literal|"MaxClientTotalTP"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|KEY_MIN_CLIENT_AVE_TP
init|=
literal|"MinClientAveTP"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|KEY_MAX_CLIENT_AVE_TP
init|=
literal|"MaxClientAveTP"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|KEY_MIN_CLIENT_AVE_EMM_TP
init|=
literal|"MinClientAveEMMTP"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|KEY_MAX_CLIENT_AVE_EMM_TP
init|=
literal|"MaxClientAveEMMTP"
decl_stmt|;
specifier|protected
name|Map
name|clientThroughputs
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
specifier|public
name|void
name|handleCsvData
parameter_list|(
name|String
name|csvData
parameter_list|)
block|{
name|StringTokenizer
name|tokenizer
init|=
operator|new
name|StringTokenizer
argument_list|(
name|csvData
argument_list|,
literal|","
argument_list|)
decl_stmt|;
name|String
name|data
decl_stmt|,
name|key
decl_stmt|,
name|val
decl_stmt|,
name|clientName
init|=
literal|null
decl_stmt|;
name|Long
name|throughput
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|tokenizer
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|data
operator|=
name|tokenizer
operator|.
name|nextToken
argument_list|()
expr_stmt|;
name|key
operator|=
name|data
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|data
operator|.
name|indexOf
argument_list|(
literal|"="
argument_list|)
argument_list|)
expr_stmt|;
name|val
operator|=
name|data
operator|.
name|substring
argument_list|(
name|data
operator|.
name|indexOf
argument_list|(
literal|"="
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|key
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"clientName"
argument_list|)
condition|)
block|{
name|clientName
operator|=
name|val
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|key
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"throughput"
argument_list|)
condition|)
block|{
name|throughput
operator|=
name|Long
operator|.
name|valueOf
argument_list|(
name|val
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Ignore unknown token
block|}
block|}
name|addToClientTPList
argument_list|(
name|clientName
argument_list|,
name|throughput
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Map
name|getSummary
parameter_list|()
block|{
comment|// Check if tp sampler wasn't used.
if|if
condition|(
name|clientThroughputs
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
operator|new
name|HashMap
argument_list|()
return|;
block|}
name|long
name|minClientTP
init|=
name|Long
operator|.
name|MAX_VALUE
decl_stmt|,
comment|// TP = throughput
name|maxClientTP
init|=
name|Long
operator|.
name|MIN_VALUE
decl_stmt|,
name|minClientTotalTP
init|=
name|Long
operator|.
name|MAX_VALUE
decl_stmt|,
name|maxClientTotalTP
init|=
name|Long
operator|.
name|MIN_VALUE
decl_stmt|,
name|systemTotalTP
init|=
literal|0
decl_stmt|;
name|double
name|minClientAveTP
init|=
name|Double
operator|.
name|MAX_VALUE
decl_stmt|,
name|maxClientAveTP
init|=
name|Double
operator|.
name|MIN_VALUE
decl_stmt|,
name|minClientAveEMMTP
init|=
name|Double
operator|.
name|MAX_VALUE
decl_stmt|,
comment|// EMM = Excluding Min/Max
name|maxClientAveEMMTP
init|=
name|Double
operator|.
name|MIN_VALUE
decl_stmt|,
name|systemAveTP
init|=
literal|0.0
decl_stmt|,
name|systemAveEMMTP
init|=
literal|0.0
decl_stmt|;
name|String
name|nameMinClientTP
init|=
literal|""
decl_stmt|,
name|nameMaxClientTP
init|=
literal|""
decl_stmt|,
name|nameMinClientTotalTP
init|=
literal|""
decl_stmt|,
name|nameMaxClientTotalTP
init|=
literal|""
decl_stmt|,
name|nameMinClientAveTP
init|=
literal|""
decl_stmt|,
name|nameMaxClientAveTP
init|=
literal|""
decl_stmt|,
name|nameMinClientAveEMMTP
init|=
literal|""
decl_stmt|,
name|nameMaxClientAveEMMTP
init|=
literal|""
decl_stmt|;
name|Set
name|clientNames
init|=
name|clientThroughputs
operator|.
name|keySet
argument_list|()
decl_stmt|;
name|String
name|clientName
decl_stmt|;
name|List
name|clientTPList
decl_stmt|;
name|long
name|tempLong
decl_stmt|;
name|double
name|tempDouble
decl_stmt|;
name|int
name|clientCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|clientNames
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|clientName
operator|=
operator|(
name|String
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|clientTPList
operator|=
operator|(
name|List
operator|)
name|clientThroughputs
operator|.
name|get
argument_list|(
name|clientName
argument_list|)
expr_stmt|;
name|clientCount
operator|++
expr_stmt|;
name|tempLong
operator|=
name|PerformanceStatisticsUtil
operator|.
name|getMin
argument_list|(
name|clientTPList
argument_list|)
expr_stmt|;
if|if
condition|(
name|tempLong
operator|<
name|minClientTP
condition|)
block|{
name|minClientTP
operator|=
name|tempLong
expr_stmt|;
name|nameMinClientTP
operator|=
name|clientName
expr_stmt|;
block|}
name|tempLong
operator|=
name|PerformanceStatisticsUtil
operator|.
name|getMax
argument_list|(
name|clientTPList
argument_list|)
expr_stmt|;
if|if
condition|(
name|tempLong
operator|>
name|maxClientTP
condition|)
block|{
name|maxClientTP
operator|=
name|tempLong
expr_stmt|;
name|nameMaxClientTP
operator|=
name|clientName
expr_stmt|;
block|}
name|tempLong
operator|=
name|PerformanceStatisticsUtil
operator|.
name|getSum
argument_list|(
name|clientTPList
argument_list|)
expr_stmt|;
name|systemTotalTP
operator|+=
name|tempLong
expr_stmt|;
comment|// Accumulate total TP
if|if
condition|(
name|tempLong
operator|<
name|minClientTotalTP
condition|)
block|{
name|minClientTotalTP
operator|=
name|tempLong
expr_stmt|;
name|nameMinClientTotalTP
operator|=
name|clientName
expr_stmt|;
block|}
if|if
condition|(
name|tempLong
operator|>
name|maxClientTotalTP
condition|)
block|{
name|maxClientTotalTP
operator|=
name|tempLong
expr_stmt|;
name|nameMaxClientTotalTP
operator|=
name|clientName
expr_stmt|;
block|}
name|tempDouble
operator|=
name|PerformanceStatisticsUtil
operator|.
name|getAve
argument_list|(
name|clientTPList
argument_list|)
expr_stmt|;
name|systemAveTP
operator|+=
name|tempDouble
expr_stmt|;
comment|// Accumulate ave throughput
if|if
condition|(
name|tempDouble
operator|<
name|minClientAveTP
condition|)
block|{
name|minClientAveTP
operator|=
name|tempDouble
expr_stmt|;
name|nameMinClientAveTP
operator|=
name|clientName
expr_stmt|;
block|}
if|if
condition|(
name|tempDouble
operator|>
name|maxClientAveTP
condition|)
block|{
name|maxClientAveTP
operator|=
name|tempDouble
expr_stmt|;
name|nameMaxClientAveTP
operator|=
name|clientName
expr_stmt|;
block|}
name|tempDouble
operator|=
name|PerformanceStatisticsUtil
operator|.
name|getAveEx
argument_list|(
name|clientTPList
argument_list|)
expr_stmt|;
name|systemAveEMMTP
operator|+=
name|tempDouble
expr_stmt|;
comment|// Accumulate ave throughput excluding min/max
if|if
condition|(
name|tempDouble
operator|<
name|minClientAveEMMTP
condition|)
block|{
name|minClientAveEMMTP
operator|=
name|tempDouble
expr_stmt|;
name|nameMinClientAveEMMTP
operator|=
name|clientName
expr_stmt|;
block|}
if|if
condition|(
name|tempDouble
operator|>
name|maxClientAveEMMTP
condition|)
block|{
name|maxClientAveEMMTP
operator|=
name|tempDouble
expr_stmt|;
name|nameMaxClientAveEMMTP
operator|=
name|clientName
expr_stmt|;
block|}
block|}
name|Map
name|summary
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|summary
operator|.
name|put
argument_list|(
name|KEY_SYS_TOTAL_TP
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|systemTotalTP
argument_list|)
argument_list|)
expr_stmt|;
name|summary
operator|.
name|put
argument_list|(
name|KEY_SYS_TOTAL_CLIENTS
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|clientCount
argument_list|)
argument_list|)
expr_stmt|;
name|summary
operator|.
name|put
argument_list|(
name|KEY_SYS_AVE_TP
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|systemAveTP
argument_list|)
argument_list|)
expr_stmt|;
name|summary
operator|.
name|put
argument_list|(
name|KEY_SYS_AVE_EMM_TP
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|systemAveEMMTP
argument_list|)
argument_list|)
expr_stmt|;
name|summary
operator|.
name|put
argument_list|(
name|KEY_SYS_AVE_CLIENT_TP
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|systemAveTP
operator|/
name|clientCount
argument_list|)
argument_list|)
expr_stmt|;
name|summary
operator|.
name|put
argument_list|(
name|KEY_SYS_AVE_CLIENT_EMM_TP
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|systemAveEMMTP
operator|/
name|clientCount
argument_list|)
argument_list|)
expr_stmt|;
name|summary
operator|.
name|put
argument_list|(
name|KEY_MIN_CLIENT_TP
argument_list|,
name|nameMinClientTP
operator|+
literal|"="
operator|+
name|minClientTP
argument_list|)
expr_stmt|;
name|summary
operator|.
name|put
argument_list|(
name|KEY_MAX_CLIENT_TP
argument_list|,
name|nameMaxClientTP
operator|+
literal|"="
operator|+
name|maxClientTP
argument_list|)
expr_stmt|;
name|summary
operator|.
name|put
argument_list|(
name|KEY_MIN_CLIENT_TOTAL_TP
argument_list|,
name|nameMinClientTotalTP
operator|+
literal|"="
operator|+
name|minClientTotalTP
argument_list|)
expr_stmt|;
name|summary
operator|.
name|put
argument_list|(
name|KEY_MAX_CLIENT_TOTAL_TP
argument_list|,
name|nameMaxClientTotalTP
operator|+
literal|"="
operator|+
name|maxClientTotalTP
argument_list|)
expr_stmt|;
name|summary
operator|.
name|put
argument_list|(
name|KEY_MIN_CLIENT_AVE_TP
argument_list|,
name|nameMinClientAveTP
operator|+
literal|"="
operator|+
name|minClientAveTP
argument_list|)
expr_stmt|;
name|summary
operator|.
name|put
argument_list|(
name|KEY_MAX_CLIENT_AVE_TP
argument_list|,
name|nameMaxClientAveTP
operator|+
literal|"="
operator|+
name|maxClientAveTP
argument_list|)
expr_stmt|;
name|summary
operator|.
name|put
argument_list|(
name|KEY_MIN_CLIENT_AVE_EMM_TP
argument_list|,
name|nameMinClientAveEMMTP
operator|+
literal|"="
operator|+
name|minClientAveEMMTP
argument_list|)
expr_stmt|;
name|summary
operator|.
name|put
argument_list|(
name|KEY_MAX_CLIENT_AVE_EMM_TP
argument_list|,
name|nameMaxClientAveEMMTP
operator|+
literal|"="
operator|+
name|maxClientAveEMMTP
argument_list|)
expr_stmt|;
return|return
name|summary
return|;
block|}
specifier|protected
name|void
name|addToClientTPList
parameter_list|(
name|String
name|clientName
parameter_list|,
name|Long
name|throughput
parameter_list|)
block|{
comment|// Write to client's throughput list
if|if
condition|(
name|clientName
operator|==
literal|null
operator|||
name|throughput
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid Throughput CSV Data: clientName="
operator|+
name|clientName
operator|+
literal|", throughput="
operator|+
name|throughput
argument_list|)
throw|;
block|}
name|List
name|clientTPList
init|=
operator|(
name|List
operator|)
name|clientThroughputs
operator|.
name|get
argument_list|(
name|clientName
argument_list|)
decl_stmt|;
if|if
condition|(
name|clientTPList
operator|==
literal|null
condition|)
block|{
name|clientTPList
operator|=
operator|new
name|ArrayList
argument_list|()
expr_stmt|;
name|clientThroughputs
operator|.
name|put
argument_list|(
name|clientName
argument_list|,
name|clientTPList
argument_list|)
expr_stmt|;
block|}
name|clientTPList
operator|.
name|add
argument_list|(
name|throughput
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit


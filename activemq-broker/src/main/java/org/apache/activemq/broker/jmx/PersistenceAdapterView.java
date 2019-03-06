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
name|jmx
package|;
end_package

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|databind
operator|.
name|ObjectMapper
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
name|management
operator|.
name|TimeStatisticImpl
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
name|store
operator|.
name|PersistenceAdapter
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
name|store
operator|.
name|PersistenceAdapterStatistics
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Callable
import|;
end_import

begin_class
specifier|public
class|class
name|PersistenceAdapterView
implements|implements
name|PersistenceAdapterViewMBean
block|{
specifier|private
specifier|final
specifier|static
name|ObjectMapper
name|mapper
init|=
operator|new
name|ObjectMapper
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
specifier|private
specifier|final
name|PersistenceAdapter
name|persistenceAdapter
decl_stmt|;
specifier|private
name|Callable
argument_list|<
name|String
argument_list|>
name|inflightTransactionViewCallable
decl_stmt|;
specifier|private
name|Callable
argument_list|<
name|String
argument_list|>
name|dataViewCallable
decl_stmt|;
specifier|private
name|PersistenceAdapterStatistics
name|persistenceAdapterStatistics
decl_stmt|;
specifier|public
name|PersistenceAdapterView
parameter_list|(
name|PersistenceAdapter
name|adapter
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|adapter
operator|.
name|toString
argument_list|()
expr_stmt|;
name|this
operator|.
name|persistenceAdapter
operator|=
name|adapter
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getTransactions
parameter_list|()
block|{
return|return
name|invoke
argument_list|(
name|inflightTransactionViewCallable
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getData
parameter_list|()
block|{
return|return
name|invoke
argument_list|(
name|dataViewCallable
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getSize
parameter_list|()
block|{
return|return
name|persistenceAdapter
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getStatistics
parameter_list|()
block|{
return|return
name|serializePersistenceAdapterStatistics
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|resetStatistics
parameter_list|()
block|{
specifier|final
name|String
name|result
init|=
name|serializePersistenceAdapterStatistics
argument_list|()
decl_stmt|;
if|if
condition|(
name|persistenceAdapterStatistics
operator|!=
literal|null
condition|)
block|{
name|persistenceAdapterStatistics
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|private
name|String
name|invoke
parameter_list|(
name|Callable
argument_list|<
name|String
argument_list|>
name|callable
parameter_list|)
block|{
name|String
name|result
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|callable
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|result
operator|=
name|callable
operator|.
name|call
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|result
operator|=
name|e
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
specifier|private
name|String
name|serializePersistenceAdapterStatistics
parameter_list|()
block|{
if|if
condition|(
name|persistenceAdapterStatistics
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|result
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|result
operator|.
name|put
argument_list|(
literal|"writeTime"
argument_list|,
name|getTimeStatisticAsMap
argument_list|(
name|persistenceAdapterStatistics
operator|.
name|getWriteTime
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|.
name|put
argument_list|(
literal|"readTime"
argument_list|,
name|getTimeStatisticAsMap
argument_list|(
name|persistenceAdapterStatistics
operator|.
name|getReadTime
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|mapper
operator|.
name|writeValueAsString
argument_list|(
name|result
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
return|return
name|e
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getTimeStatisticAsMap
parameter_list|(
specifier|final
name|TimeStatisticImpl
name|timeStatistic
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|result
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|result
operator|.
name|put
argument_list|(
literal|"count"
argument_list|,
name|timeStatistic
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|put
argument_list|(
literal|"maxTime"
argument_list|,
name|timeStatistic
operator|.
name|getMaxTime
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|put
argument_list|(
literal|"minTime"
argument_list|,
name|timeStatistic
operator|.
name|getMinTime
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|put
argument_list|(
literal|"totalTime"
argument_list|,
name|timeStatistic
operator|.
name|getTotalTime
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|put
argument_list|(
literal|"averageTime"
argument_list|,
name|timeStatistic
operator|.
name|getAverageTime
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|put
argument_list|(
literal|"averageTimeExMinMax"
argument_list|,
name|timeStatistic
operator|.
name|getAverageTimeExcludingMinMax
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|put
argument_list|(
literal|"averagePerSecond"
argument_list|,
name|timeStatistic
operator|.
name|getAveragePerSecond
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|put
argument_list|(
literal|"averagePerSecondExMinMax"
argument_list|,
name|timeStatistic
operator|.
name|getAveragePerSecondExcludingMinMax
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
specifier|public
name|void
name|setDataViewCallable
parameter_list|(
name|Callable
argument_list|<
name|String
argument_list|>
name|dataViewCallable
parameter_list|)
block|{
name|this
operator|.
name|dataViewCallable
operator|=
name|dataViewCallable
expr_stmt|;
block|}
specifier|public
name|void
name|setInflightTransactionViewCallable
parameter_list|(
name|Callable
argument_list|<
name|String
argument_list|>
name|inflightTransactionViewCallable
parameter_list|)
block|{
name|this
operator|.
name|inflightTransactionViewCallable
operator|=
name|inflightTransactionViewCallable
expr_stmt|;
block|}
specifier|public
name|void
name|setPersistenceAdapterStatistics
parameter_list|(
name|PersistenceAdapterStatistics
name|persistenceAdapterStatistics
parameter_list|)
block|{
name|this
operator|.
name|persistenceAdapterStatistics
operator|=
name|persistenceAdapterStatistics
expr_stmt|;
block|}
block|}
end_class

end_unit


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
name|*
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|j2ee
operator|.
name|statistics
operator|.
name|Statistic
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|j2ee
operator|.
name|statistics
operator|.
name|Stats
import|;
end_import

begin_comment
comment|/**  * Base class for a Stats implementation  *   * @version $Revision: 1.2 $  */
end_comment

begin_class
specifier|public
class|class
name|StatsImpl
extends|extends
name|StatisticImpl
implements|implements
name|Stats
implements|,
name|Resettable
block|{
specifier|private
name|Map
name|map
decl_stmt|;
specifier|public
name|StatsImpl
parameter_list|()
block|{
name|this
argument_list|(
operator|new
name|HashMap
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|StatsImpl
parameter_list|(
name|Map
name|map
parameter_list|)
block|{
name|super
argument_list|(
literal|"stats"
argument_list|,
literal|"many"
argument_list|,
literal|"Used only as container, not Statistic"
argument_list|)
expr_stmt|;
name|this
operator|.
name|map
operator|=
name|map
expr_stmt|;
block|}
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|Statistic
index|[]
name|stats
init|=
name|getStatistics
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|,
name|size
init|=
name|stats
operator|.
name|length
init|;
name|i
operator|<
name|size
condition|;
name|i
operator|++
control|)
block|{
name|Statistic
name|stat
init|=
name|stats
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|stat
operator|instanceof
name|Resettable
condition|)
block|{
name|Resettable
name|r
init|=
operator|(
name|Resettable
operator|)
name|stat
decl_stmt|;
name|r
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|Statistic
name|getStatistic
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|(
name|Statistic
operator|)
name|map
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
specifier|public
name|String
index|[]
name|getStatisticNames
parameter_list|()
block|{
name|Set
name|keys
init|=
name|map
operator|.
name|keySet
argument_list|()
decl_stmt|;
name|String
index|[]
name|answer
init|=
operator|new
name|String
index|[
name|keys
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|keys
operator|.
name|toArray
argument_list|(
name|answer
argument_list|)
expr_stmt|;
return|return
name|answer
return|;
block|}
specifier|public
name|Statistic
index|[]
name|getStatistics
parameter_list|()
block|{
name|Collection
name|values
init|=
name|map
operator|.
name|values
argument_list|()
decl_stmt|;
name|Statistic
index|[]
name|answer
init|=
operator|new
name|Statistic
index|[
name|values
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|values
operator|.
name|toArray
argument_list|(
name|answer
argument_list|)
expr_stmt|;
return|return
name|answer
return|;
block|}
specifier|protected
name|void
name|addStatistic
parameter_list|(
name|String
name|name
parameter_list|,
name|StatisticImpl
name|statistic
parameter_list|)
block|{
name|map
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|statistic
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit


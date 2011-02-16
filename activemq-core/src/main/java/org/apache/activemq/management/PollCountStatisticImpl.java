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
name|ArrayList
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
name|java
operator|.
name|util
operator|.
name|List
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
name|CountStatistic
import|;
end_import

begin_comment
comment|/**  * A count statistic implementation  *   *   */
end_comment

begin_class
specifier|public
class|class
name|PollCountStatisticImpl
extends|extends
name|StatisticImpl
implements|implements
name|CountStatistic
block|{
specifier|private
name|PollCountStatisticImpl
name|parent
decl_stmt|;
specifier|private
name|List
argument_list|<
name|PollCountStatisticImpl
argument_list|>
name|children
decl_stmt|;
specifier|public
name|PollCountStatisticImpl
parameter_list|(
name|PollCountStatisticImpl
name|parent
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|description
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|description
argument_list|)
expr_stmt|;
name|setParent
argument_list|(
name|parent
argument_list|)
expr_stmt|;
block|}
specifier|public
name|PollCountStatisticImpl
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|description
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
literal|"count"
argument_list|,
name|description
argument_list|)
expr_stmt|;
block|}
specifier|public
name|PollCountStatisticImpl
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|unit
parameter_list|,
name|String
name|description
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|unit
argument_list|,
name|description
argument_list|)
expr_stmt|;
block|}
specifier|public
name|PollCountStatisticImpl
name|getParent
parameter_list|()
block|{
return|return
name|parent
return|;
block|}
specifier|public
name|void
name|setParent
parameter_list|(
name|PollCountStatisticImpl
name|parent
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|parent
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|parent
operator|.
name|removeChild
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|parent
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|parent
operator|.
name|addChild
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|synchronized
name|void
name|removeChild
parameter_list|(
name|PollCountStatisticImpl
name|child
parameter_list|)
block|{
if|if
condition|(
name|children
operator|!=
literal|null
condition|)
block|{
name|children
operator|.
name|remove
argument_list|(
name|child
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|synchronized
name|void
name|addChild
parameter_list|(
name|PollCountStatisticImpl
name|child
parameter_list|)
block|{
if|if
condition|(
name|children
operator|==
literal|null
condition|)
block|{
name|children
operator|=
operator|new
name|ArrayList
argument_list|<
name|PollCountStatisticImpl
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|children
operator|.
name|add
argument_list|(
name|child
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|long
name|getCount
parameter_list|()
block|{
if|if
condition|(
name|children
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
name|long
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|PollCountStatisticImpl
argument_list|>
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
name|PollCountStatisticImpl
name|child
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|count
operator|+=
name|child
operator|.
name|getCount
argument_list|()
expr_stmt|;
block|}
return|return
name|count
return|;
block|}
specifier|protected
name|void
name|appendFieldDescription
parameter_list|(
name|StringBuffer
name|buffer
parameter_list|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|" count: "
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|Long
operator|.
name|toString
argument_list|(
name|getCount
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|super
operator|.
name|appendFieldDescription
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return the average time period that elapses between counter increments      *         since the last reset.      */
specifier|public
name|double
name|getPeriod
parameter_list|()
block|{
name|double
name|count
init|=
name|getCount
argument_list|()
decl_stmt|;
if|if
condition|(
name|count
operator|==
literal|0
condition|)
block|{
return|return
literal|0
return|;
block|}
name|double
name|time
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|getStartTime
argument_list|()
decl_stmt|;
return|return
name|time
operator|/
operator|(
name|count
operator|*
literal|1000.0
operator|)
return|;
block|}
comment|/**      * @return the number of times per second that the counter is incrementing      *         since the last reset.      */
specifier|public
name|double
name|getFrequency
parameter_list|()
block|{
name|double
name|count
init|=
name|getCount
argument_list|()
decl_stmt|;
name|double
name|time
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|getStartTime
argument_list|()
decl_stmt|;
return|return
name|count
operator|*
literal|1000.0
operator|/
name|time
return|;
block|}
block|}
end_class

end_unit


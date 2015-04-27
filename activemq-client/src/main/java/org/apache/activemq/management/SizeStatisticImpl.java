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

begin_class
specifier|public
class|class
name|SizeStatisticImpl
extends|extends
name|StatisticImpl
block|{
specifier|private
name|long
name|count
decl_stmt|;
specifier|private
name|long
name|maxSize
decl_stmt|;
specifier|private
name|long
name|minSize
decl_stmt|;
specifier|private
name|long
name|totalSize
decl_stmt|;
specifier|private
name|SizeStatisticImpl
name|parent
decl_stmt|;
specifier|public
name|SizeStatisticImpl
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
literal|"bytes"
argument_list|,
name|description
argument_list|)
expr_stmt|;
block|}
specifier|public
name|SizeStatisticImpl
parameter_list|(
name|SizeStatisticImpl
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
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
block|}
specifier|public
name|SizeStatisticImpl
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
annotation|@
name|Override
specifier|public
specifier|synchronized
name|void
name|reset
parameter_list|()
block|{
if|if
condition|(
name|isDoReset
argument_list|()
condition|)
block|{
name|super
operator|.
name|reset
argument_list|()
expr_stmt|;
name|count
operator|=
literal|0
expr_stmt|;
name|maxSize
operator|=
literal|0
expr_stmt|;
name|minSize
operator|=
literal|0
expr_stmt|;
name|totalSize
operator|=
literal|0
expr_stmt|;
block|}
block|}
specifier|public
specifier|synchronized
name|long
name|getCount
parameter_list|()
block|{
return|return
name|count
return|;
block|}
specifier|public
specifier|synchronized
name|void
name|addSize
parameter_list|(
name|long
name|size
parameter_list|)
block|{
name|count
operator|++
expr_stmt|;
name|totalSize
operator|+=
name|size
expr_stmt|;
if|if
condition|(
name|size
operator|>
name|maxSize
condition|)
block|{
name|maxSize
operator|=
name|size
expr_stmt|;
block|}
if|if
condition|(
name|size
operator|<
name|minSize
operator|||
name|minSize
operator|==
literal|0
condition|)
block|{
name|minSize
operator|=
name|size
expr_stmt|;
block|}
name|updateSampleTime
argument_list|()
expr_stmt|;
if|if
condition|(
name|parent
operator|!=
literal|null
condition|)
block|{
name|parent
operator|.
name|addSize
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Reset the total size to the new value      *      * @param size      */
specifier|public
specifier|synchronized
name|void
name|setTotalSize
parameter_list|(
name|long
name|size
parameter_list|)
block|{
name|count
operator|++
expr_stmt|;
name|totalSize
operator|=
name|size
expr_stmt|;
if|if
condition|(
name|size
operator|>
name|maxSize
condition|)
block|{
name|maxSize
operator|=
name|size
expr_stmt|;
block|}
if|if
condition|(
name|size
operator|<
name|minSize
operator|||
name|minSize
operator|==
literal|0
condition|)
block|{
name|minSize
operator|=
name|size
expr_stmt|;
block|}
name|updateSampleTime
argument_list|()
expr_stmt|;
block|}
comment|/**      * @return the maximum size of any step      */
specifier|public
name|long
name|getMaxSize
parameter_list|()
block|{
return|return
name|maxSize
return|;
block|}
comment|/**      * @return the minimum size of any step      */
specifier|public
specifier|synchronized
name|long
name|getMinSize
parameter_list|()
block|{
return|return
name|minSize
return|;
block|}
comment|/**      * @return the total size of all the steps added together      */
specifier|public
specifier|synchronized
name|long
name|getTotalSize
parameter_list|()
block|{
return|return
name|totalSize
return|;
block|}
comment|/**      * @return the average size calculated by dividing the total size by the      *         number of counts      */
specifier|public
specifier|synchronized
name|double
name|getAverageSize
parameter_list|()
block|{
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
name|d
init|=
name|totalSize
decl_stmt|;
return|return
name|d
operator|/
name|count
return|;
block|}
comment|/**      * @return the average size calculated by dividing the total size by the      *         number of counts but excluding the minimum and maximum sizes.      */
specifier|public
specifier|synchronized
name|double
name|getAverageSizeExcludingMinMax
parameter_list|()
block|{
if|if
condition|(
name|count
operator|<=
literal|2
condition|)
block|{
return|return
literal|0
return|;
block|}
name|double
name|d
init|=
name|totalSize
operator|-
name|minSize
operator|-
name|maxSize
decl_stmt|;
return|return
name|d
operator|/
operator|(
name|count
operator|-
literal|2
operator|)
return|;
block|}
comment|/**      * @return the average number of steps per second      */
specifier|public
name|double
name|getAveragePerSecond
parameter_list|()
block|{
name|double
name|d
init|=
literal|1000
decl_stmt|;
name|double
name|averageSize
init|=
name|getAverageSize
argument_list|()
decl_stmt|;
if|if
condition|(
name|averageSize
operator|==
literal|0
condition|)
block|{
return|return
literal|0
return|;
block|}
return|return
name|d
operator|/
name|averageSize
return|;
block|}
comment|/**      * @return the average number of steps per second excluding the min& max      *         values      */
specifier|public
name|double
name|getAveragePerSecondExcludingMinMax
parameter_list|()
block|{
name|double
name|d
init|=
literal|1000
decl_stmt|;
name|double
name|average
init|=
name|getAverageSizeExcludingMinMax
argument_list|()
decl_stmt|;
if|if
condition|(
name|average
operator|==
literal|0
condition|)
block|{
return|return
literal|0
return|;
block|}
return|return
name|d
operator|/
name|average
return|;
block|}
specifier|public
name|SizeStatisticImpl
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
name|SizeStatisticImpl
name|parent
parameter_list|)
block|{
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
specifier|synchronized
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
name|count
argument_list|)
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|" maxSize: "
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
name|maxSize
argument_list|)
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|" minSize: "
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
name|minSize
argument_list|)
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|" totalSize: "
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
name|totalSize
argument_list|)
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|" averageSize: "
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|Double
operator|.
name|toString
argument_list|(
name|getAverageSize
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|" averageTimeExMinMax: "
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|Double
operator|.
name|toString
argument_list|(
name|getAveragePerSecondExcludingMinMax
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|" averagePerSecond: "
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|Double
operator|.
name|toString
argument_list|(
name|getAveragePerSecond
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|" averagePerSecondExMinMax: "
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|Double
operator|.
name|toString
argument_list|(
name|getAveragePerSecondExcludingMinMax
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
block|}
end_class

end_unit


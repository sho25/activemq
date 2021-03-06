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
name|store
operator|.
name|kahadb
operator|.
name|disk
operator|.
name|util
package|;
end_package

begin_comment
comment|/**  * Represents a range of numbers.  *   * @author chirino  */
end_comment

begin_class
specifier|public
class|class
name|Sequence
extends|extends
name|LinkedNode
argument_list|<
name|Sequence
argument_list|>
block|{
name|long
name|first
decl_stmt|;
name|long
name|last
decl_stmt|;
specifier|public
name|Sequence
parameter_list|(
name|long
name|value
parameter_list|)
block|{
name|first
operator|=
name|last
operator|=
name|value
expr_stmt|;
block|}
specifier|public
name|Sequence
parameter_list|(
name|long
name|first
parameter_list|,
name|long
name|last
parameter_list|)
block|{
name|this
operator|.
name|first
operator|=
name|first
expr_stmt|;
name|this
operator|.
name|last
operator|=
name|last
expr_stmt|;
block|}
specifier|public
name|boolean
name|isAdjacentToLast
parameter_list|(
name|long
name|value
parameter_list|)
block|{
return|return
name|last
operator|+
literal|1
operator|==
name|value
return|;
block|}
specifier|public
name|boolean
name|isBiggerButNotAdjacentToLast
parameter_list|(
name|long
name|value
parameter_list|)
block|{
return|return
name|last
operator|+
literal|1
operator|<
name|value
return|;
block|}
specifier|public
name|boolean
name|isAdjacentToFirst
parameter_list|(
name|long
name|value
parameter_list|)
block|{
return|return
name|first
operator|-
literal|1
operator|==
name|value
return|;
block|}
specifier|public
name|boolean
name|contains
parameter_list|(
name|long
name|value
parameter_list|)
block|{
return|return
name|first
operator|<=
name|value
operator|&&
name|value
operator|<=
name|last
return|;
block|}
specifier|public
name|long
name|range
parameter_list|()
block|{
return|return
name|first
operator|==
name|last
condition|?
literal|1
else|:
operator|(
name|last
operator|-
name|first
operator|)
operator|+
literal|1
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|first
operator|==
name|last
condition|?
literal|""
operator|+
name|first
else|:
name|first
operator|+
literal|".."
operator|+
name|last
return|;
block|}
specifier|public
name|long
name|getFirst
parameter_list|()
block|{
return|return
name|first
return|;
block|}
specifier|public
name|void
name|setFirst
parameter_list|(
name|long
name|first
parameter_list|)
block|{
name|this
operator|.
name|first
operator|=
name|first
expr_stmt|;
block|}
specifier|public
name|long
name|getLast
parameter_list|()
block|{
return|return
name|last
return|;
block|}
specifier|public
name|void
name|setLast
parameter_list|(
name|long
name|last
parameter_list|)
block|{
name|this
operator|.
name|last
operator|=
name|last
expr_stmt|;
block|}
specifier|public
interface|interface
name|Closure
parameter_list|<
name|T
extends|extends
name|Throwable
parameter_list|>
block|{
specifier|public
name|void
name|execute
parameter_list|(
name|long
name|value
parameter_list|)
throws|throws
name|T
function_decl|;
block|}
specifier|public
parameter_list|<
name|T
extends|extends
name|Throwable
parameter_list|>
name|void
name|each
parameter_list|(
name|Closure
argument_list|<
name|T
argument_list|>
name|closure
parameter_list|)
throws|throws
name|T
block|{
for|for
control|(
name|long
name|i
init|=
name|first
init|;
name|i
operator|<=
name|last
condition|;
name|i
operator|++
control|)
block|{
name|closure
operator|.
name|execute
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit


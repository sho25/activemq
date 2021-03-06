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
name|region
operator|.
name|policy
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
name|broker
operator|.
name|region
operator|.
name|MessageReference
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
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
import|;
end_import

begin_comment
comment|/**  * An eviction strategy which evicts the oldest message within messages with the same property value  *  *  * @org.apache.xbean.XBean  *  */
end_comment

begin_class
specifier|public
class|class
name|UniquePropertyMessageEvictionStrategy
extends|extends
name|MessageEvictionStrategySupport
block|{
specifier|protected
name|String
name|propertyName
decl_stmt|;
specifier|public
name|String
name|getPropertyName
parameter_list|()
block|{
return|return
name|propertyName
return|;
block|}
specifier|public
name|void
name|setPropertyName
parameter_list|(
name|String
name|propertyName
parameter_list|)
block|{
name|this
operator|.
name|propertyName
operator|=
name|propertyName
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|MessageReference
index|[]
name|evictMessages
parameter_list|(
name|LinkedList
name|messages
parameter_list|)
throws|throws
name|IOException
block|{
name|MessageReference
name|oldest
init|=
operator|(
name|MessageReference
operator|)
name|messages
operator|.
name|getFirst
argument_list|()
decl_stmt|;
name|HashMap
argument_list|<
name|Object
argument_list|,
name|MessageReference
argument_list|>
name|pivots
init|=
operator|new
name|HashMap
argument_list|<
name|Object
argument_list|,
name|MessageReference
argument_list|>
argument_list|()
decl_stmt|;
name|Iterator
name|iter
init|=
name|messages
operator|.
name|iterator
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|MessageReference
name|reference
init|=
operator|(
name|MessageReference
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|propertyName
operator|!=
literal|null
operator|&&
name|reference
operator|.
name|getMessage
argument_list|()
operator|.
name|getProperty
argument_list|(
name|propertyName
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|Object
name|key
init|=
name|reference
operator|.
name|getMessage
argument_list|()
operator|.
name|getProperty
argument_list|(
name|propertyName
argument_list|)
decl_stmt|;
if|if
condition|(
name|pivots
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
condition|)
block|{
name|MessageReference
name|pivot
init|=
name|pivots
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|reference
operator|.
name|getMessage
argument_list|()
operator|.
name|getTimestamp
argument_list|()
operator|>
name|pivot
operator|.
name|getMessage
argument_list|()
operator|.
name|getTimestamp
argument_list|()
condition|)
block|{
name|pivots
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|reference
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|pivots
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|reference
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
operator|!
name|pivots
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
for|for
control|(
name|MessageReference
name|ref
range|:
name|pivots
operator|.
name|values
argument_list|()
control|)
block|{
name|messages
operator|.
name|remove
argument_list|(
name|ref
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|messages
operator|.
name|size
argument_list|()
operator|!=
literal|0
condition|)
block|{
return|return
operator|(
name|MessageReference
index|[]
operator|)
name|messages
operator|.
name|toArray
argument_list|(
operator|new
name|MessageReference
index|[
name|messages
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
block|}
return|return
operator|new
name|MessageReference
index|[]
block|{
name|oldest
block|}
return|;
block|}
block|}
end_class

end_unit


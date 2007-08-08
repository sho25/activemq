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
name|memory
package|;
end_package

begin_class
specifier|public
class|class
name|CacheEntry
block|{
specifier|public
specifier|final
name|Object
name|key
decl_stmt|;
specifier|public
specifier|final
name|Object
name|value
decl_stmt|;
specifier|public
name|CacheEntry
name|next
decl_stmt|;
specifier|public
name|CacheEntry
name|previous
decl_stmt|;
specifier|public
name|CacheEntryList
name|owner
decl_stmt|;
specifier|public
name|CacheEntry
parameter_list|(
name|Object
name|key
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
comment|/**      *       * @param entry      * @return false if you are trying to remove the tail pointer.      */
specifier|public
name|boolean
name|remove
parameter_list|()
block|{
comment|// Cannot remove if this is a tail pointer.
comment|// Or not linked.
if|if
condition|(
name|owner
operator|==
literal|null
operator|||
name|this
operator|.
name|key
operator|==
literal|null
operator|||
name|this
operator|.
name|next
operator|==
literal|null
condition|)
return|return
literal|false
return|;
synchronized|synchronized
init|(
name|owner
operator|.
name|tail
init|)
block|{
name|this
operator|.
name|next
operator|.
name|previous
operator|=
name|this
operator|.
name|previous
expr_stmt|;
name|this
operator|.
name|previous
operator|.
name|next
operator|=
name|this
operator|.
name|next
expr_stmt|;
name|this
operator|.
name|owner
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|next
operator|=
name|this
operator|.
name|previous
operator|=
literal|null
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit


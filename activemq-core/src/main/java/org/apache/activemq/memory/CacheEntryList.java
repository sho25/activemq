begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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

begin_comment
comment|/**  * Maintains a simple linked list of CacheEntry objects.  It is thread safe.  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|CacheEntryList
block|{
comment|// Points at the tail of the CacheEntry list
specifier|public
specifier|final
name|CacheEntry
name|tail
init|=
operator|new
name|CacheEntry
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
specifier|public
name|CacheEntryList
parameter_list|()
block|{
name|tail
operator|.
name|next
operator|=
name|tail
operator|.
name|previous
operator|=
name|tail
expr_stmt|;
block|}
specifier|public
name|void
name|add
parameter_list|(
name|CacheEntry
name|ce
parameter_list|)
block|{
name|addEntryBefore
argument_list|(
name|tail
argument_list|,
name|ce
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|addEntryBefore
parameter_list|(
name|CacheEntry
name|position
parameter_list|,
name|CacheEntry
name|ce
parameter_list|)
block|{
assert|assert
name|ce
operator|.
name|key
operator|!=
literal|null
operator|&&
name|ce
operator|.
name|next
operator|==
literal|null
operator|&&
name|ce
operator|.
name|owner
operator|==
literal|null
assert|;
synchronized|synchronized
init|(
name|tail
init|)
block|{
name|ce
operator|.
name|owner
operator|=
name|this
expr_stmt|;
name|ce
operator|.
name|next
operator|=
name|position
expr_stmt|;
name|ce
operator|.
name|previous
operator|=
name|position
operator|.
name|previous
expr_stmt|;
name|ce
operator|.
name|previous
operator|.
name|next
operator|=
name|ce
expr_stmt|;
name|ce
operator|.
name|next
operator|.
name|previous
operator|=
name|ce
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|clear
parameter_list|()
block|{
synchronized|synchronized
init|(
name|tail
init|)
block|{
name|tail
operator|.
name|next
operator|=
name|tail
operator|.
name|previous
operator|=
name|tail
expr_stmt|;
block|}
block|}
specifier|public
name|CacheEvictor
name|createFIFOCacheEvictor
parameter_list|()
block|{
return|return
operator|new
name|CacheEvictor
argument_list|()
block|{
specifier|public
name|CacheEntry
name|evictCacheEntry
parameter_list|()
block|{
name|CacheEntry
name|rc
decl_stmt|;
synchronized|synchronized
init|(
name|tail
init|)
block|{
name|rc
operator|=
name|tail
operator|.
name|next
expr_stmt|;
block|}
return|return
name|rc
operator|.
name|remove
argument_list|()
condition|?
name|rc
else|:
literal|null
return|;
block|}
block|}
return|;
block|}
specifier|public
name|CacheEvictor
name|createLIFOCacheEvictor
parameter_list|()
block|{
return|return
operator|new
name|CacheEvictor
argument_list|()
block|{
specifier|public
name|CacheEntry
name|evictCacheEntry
parameter_list|()
block|{
name|CacheEntry
name|rc
decl_stmt|;
synchronized|synchronized
init|(
name|tail
init|)
block|{
name|rc
operator|=
name|tail
operator|.
name|previous
expr_stmt|;
block|}
return|return
name|rc
operator|.
name|remove
argument_list|()
condition|?
name|rc
else|:
literal|null
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit


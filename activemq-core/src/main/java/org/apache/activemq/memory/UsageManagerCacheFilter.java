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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
import|;
end_import

begin_comment
comment|/**  * Simple CacheFilter that increases/decreases usage on a UsageManager as  * objects are added/removed from the Cache.  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|UsageManagerCacheFilter
extends|extends
name|CacheFilter
block|{
specifier|private
specifier|final
name|AtomicLong
name|totalUsage
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|UsageManager
name|um
decl_stmt|;
specifier|public
name|UsageManagerCacheFilter
parameter_list|(
name|Cache
name|next
parameter_list|,
name|UsageManager
name|um
parameter_list|)
block|{
name|super
argument_list|(
name|next
argument_list|)
expr_stmt|;
name|this
operator|.
name|um
operator|=
name|um
expr_stmt|;
block|}
specifier|public
name|Object
name|put
parameter_list|(
name|Object
name|key
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|long
name|usage
init|=
name|getUsageOfAddedObject
argument_list|(
name|value
argument_list|)
decl_stmt|;
name|Object
name|rc
init|=
name|super
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
name|rc
operator|!=
literal|null
condition|)
block|{
name|usage
operator|-=
name|getUsageOfRemovedObject
argument_list|(
name|rc
argument_list|)
expr_stmt|;
block|}
name|totalUsage
operator|.
name|addAndGet
argument_list|(
name|usage
argument_list|)
expr_stmt|;
name|um
operator|.
name|increaseUsage
argument_list|(
name|usage
argument_list|)
expr_stmt|;
return|return
name|rc
return|;
block|}
specifier|public
name|Object
name|remove
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
name|Object
name|rc
init|=
name|super
operator|.
name|remove
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|rc
operator|!=
literal|null
condition|)
block|{
name|long
name|usage
init|=
name|getUsageOfRemovedObject
argument_list|(
name|rc
argument_list|)
decl_stmt|;
name|totalUsage
operator|.
name|addAndGet
argument_list|(
operator|-
name|usage
argument_list|)
expr_stmt|;
name|um
operator|.
name|decreaseUsage
argument_list|(
name|usage
argument_list|)
expr_stmt|;
block|}
return|return
name|rc
return|;
block|}
specifier|protected
name|long
name|getUsageOfAddedObject
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
return|return
literal|1
return|;
block|}
specifier|protected
name|long
name|getUsageOfRemovedObject
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
return|return
literal|1
return|;
block|}
specifier|public
name|void
name|close
parameter_list|()
block|{
name|um
operator|.
name|decreaseUsage
argument_list|(
name|totalUsage
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit


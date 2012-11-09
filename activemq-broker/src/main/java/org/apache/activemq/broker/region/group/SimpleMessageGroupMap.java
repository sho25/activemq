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
name|group
package|;
end_package

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
name|ConcurrentHashMap
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
name|command
operator|.
name|ConsumerId
import|;
end_import

begin_comment
comment|/**  * A simple implementation which tracks every individual GroupID value but  * which can become a memory leak if clients die before they complete a message  * group.  *   *   */
end_comment

begin_class
specifier|public
class|class
name|SimpleMessageGroupMap
implements|implements
name|MessageGroupMap
block|{
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|ConsumerId
argument_list|>
name|map
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|ConsumerId
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|void
name|put
parameter_list|(
name|String
name|groupId
parameter_list|,
name|ConsumerId
name|consumerId
parameter_list|)
block|{
name|map
operator|.
name|put
argument_list|(
name|groupId
argument_list|,
name|consumerId
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ConsumerId
name|get
parameter_list|(
name|String
name|groupId
parameter_list|)
block|{
return|return
name|map
operator|.
name|get
argument_list|(
name|groupId
argument_list|)
return|;
block|}
specifier|public
name|ConsumerId
name|removeGroup
parameter_list|(
name|String
name|groupId
parameter_list|)
block|{
return|return
name|map
operator|.
name|remove
argument_list|(
name|groupId
argument_list|)
return|;
block|}
specifier|public
name|MessageGroupSet
name|removeConsumer
parameter_list|(
name|ConsumerId
name|consumerId
parameter_list|)
block|{
name|SimpleMessageGroupSet
name|ownedGroups
init|=
operator|new
name|SimpleMessageGroupSet
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|String
argument_list|>
name|iter
init|=
name|map
operator|.
name|keySet
argument_list|()
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
name|String
name|group
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|ConsumerId
name|owner
init|=
name|map
operator|.
name|get
argument_list|(
name|group
argument_list|)
decl_stmt|;
if|if
condition|(
name|owner
operator|.
name|equals
argument_list|(
name|consumerId
argument_list|)
condition|)
block|{
name|ownedGroups
operator|.
name|add
argument_list|(
name|group
argument_list|)
expr_stmt|;
name|iter
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|ownedGroups
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"message groups: "
operator|+
name|map
operator|.
name|size
argument_list|()
return|;
block|}
block|}
end_class

end_unit

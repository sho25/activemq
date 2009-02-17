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
name|network
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|CopyOnWriteArraySet
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
name|atomic
operator|.
name|AtomicInteger
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
name|ConsumerInfo
import|;
end_import

begin_comment
comment|/**  * Represents a network bridge interface  *   * @version $Revision: 1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|DemandSubscription
block|{
specifier|private
specifier|final
name|ConsumerInfo
name|remoteInfo
decl_stmt|;
specifier|private
specifier|final
name|ConsumerInfo
name|localInfo
decl_stmt|;
specifier|private
name|Set
argument_list|<
name|ConsumerId
argument_list|>
name|remoteSubsIds
init|=
operator|new
name|CopyOnWriteArraySet
argument_list|<
name|ConsumerId
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|AtomicInteger
name|dispatched
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|DemandSubscription
parameter_list|(
name|ConsumerInfo
name|info
parameter_list|)
block|{
name|remoteInfo
operator|=
name|info
expr_stmt|;
name|localInfo
operator|=
name|info
operator|.
name|copy
argument_list|()
expr_stmt|;
name|localInfo
operator|.
name|setNetworkSubscription
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|remoteSubsIds
operator|.
name|add
argument_list|(
name|info
operator|.
name|getConsumerId
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Increment the consumers associated with this subscription      *       * @param id      * @return true if added      */
specifier|public
name|boolean
name|add
parameter_list|(
name|ConsumerId
name|id
parameter_list|)
block|{
return|return
name|remoteSubsIds
operator|.
name|add
argument_list|(
name|id
argument_list|)
return|;
block|}
comment|/**      * Increment the consumers associated with this subscription      *       * @param id      * @return true if removed      */
specifier|public
name|boolean
name|remove
parameter_list|(
name|ConsumerId
name|id
parameter_list|)
block|{
return|return
name|remoteSubsIds
operator|.
name|remove
argument_list|(
name|id
argument_list|)
return|;
block|}
comment|/**      * @return true if there are no interested consumers      */
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|remoteSubsIds
operator|.
name|isEmpty
argument_list|()
return|;
block|}
comment|/**      * @return Returns the dispatched.      */
specifier|public
name|int
name|getDispatched
parameter_list|()
block|{
return|return
name|dispatched
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**      * @param dispatched The dispatched to set.      */
specifier|public
name|void
name|setDispatched
parameter_list|(
name|int
name|dispatched
parameter_list|)
block|{
name|this
operator|.
name|dispatched
operator|.
name|set
argument_list|(
name|dispatched
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return dispatched count after incremented      */
specifier|public
name|int
name|incrementDispatched
parameter_list|()
block|{
return|return
name|dispatched
operator|.
name|incrementAndGet
argument_list|()
return|;
block|}
comment|/**      * @return Returns the localInfo.      */
specifier|public
name|ConsumerInfo
name|getLocalInfo
parameter_list|()
block|{
return|return
name|localInfo
return|;
block|}
comment|/**      * @return Returns the remoteInfo.      */
specifier|public
name|ConsumerInfo
name|getRemoteInfo
parameter_list|()
block|{
return|return
name|remoteInfo
return|;
block|}
block|}
end_class

end_unit


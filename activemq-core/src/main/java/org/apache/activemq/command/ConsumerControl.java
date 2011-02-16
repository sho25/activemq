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
name|command
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
name|state
operator|.
name|CommandVisitor
import|;
end_import

begin_comment
comment|/**  * Used to start and stop transports as well as terminating clients.  *   * @openwire:marshaller code="17"  *   */
end_comment

begin_class
specifier|public
class|class
name|ConsumerControl
extends|extends
name|BaseCommand
block|{
specifier|public
specifier|static
specifier|final
name|byte
name|DATA_STRUCTURE_TYPE
init|=
name|CommandTypes
operator|.
name|CONSUMER_CONTROL
decl_stmt|;
specifier|protected
name|ConsumerId
name|consumerId
decl_stmt|;
specifier|protected
name|boolean
name|close
decl_stmt|;
specifier|protected
name|boolean
name|stop
decl_stmt|;
specifier|protected
name|boolean
name|start
decl_stmt|;
specifier|protected
name|boolean
name|flush
decl_stmt|;
specifier|protected
name|int
name|prefetch
decl_stmt|;
specifier|protected
name|ActiveMQDestination
name|destination
decl_stmt|;
comment|/**      * @openwire:property version=6      * @return Returns the destination.      */
specifier|public
name|ActiveMQDestination
name|getDestination
parameter_list|()
block|{
return|return
name|destination
return|;
block|}
specifier|public
name|void
name|setDestination
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
name|this
operator|.
name|destination
operator|=
name|destination
expr_stmt|;
block|}
specifier|public
name|byte
name|getDataStructureType
parameter_list|()
block|{
return|return
name|DATA_STRUCTURE_TYPE
return|;
block|}
specifier|public
name|Response
name|visit
parameter_list|(
name|CommandVisitor
name|visitor
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|visitor
operator|.
name|processConsumerControl
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/**      * @openwire:property version=1      * @return Returns the close.      */
specifier|public
name|boolean
name|isClose
parameter_list|()
block|{
return|return
name|close
return|;
block|}
comment|/**      * @param close The close to set.      */
specifier|public
name|void
name|setClose
parameter_list|(
name|boolean
name|close
parameter_list|)
block|{
name|this
operator|.
name|close
operator|=
name|close
expr_stmt|;
block|}
comment|/**      * @openwire:property version=1      * @return Returns the consumerId.      */
specifier|public
name|ConsumerId
name|getConsumerId
parameter_list|()
block|{
return|return
name|consumerId
return|;
block|}
comment|/**      * @param consumerId The consumerId to set.      */
specifier|public
name|void
name|setConsumerId
parameter_list|(
name|ConsumerId
name|consumerId
parameter_list|)
block|{
name|this
operator|.
name|consumerId
operator|=
name|consumerId
expr_stmt|;
block|}
comment|/**      * @openwire:property version=1      * @return Returns the prefetch.      */
specifier|public
name|int
name|getPrefetch
parameter_list|()
block|{
return|return
name|prefetch
return|;
block|}
comment|/**      * @param prefetch The prefetch to set.      */
specifier|public
name|void
name|setPrefetch
parameter_list|(
name|int
name|prefetch
parameter_list|)
block|{
name|this
operator|.
name|prefetch
operator|=
name|prefetch
expr_stmt|;
block|}
comment|/**      * @openwire:property version=2      * @return the flush      */
specifier|public
name|boolean
name|isFlush
parameter_list|()
block|{
return|return
name|this
operator|.
name|flush
return|;
block|}
comment|/**      * @param flush the flush to set      */
specifier|public
name|void
name|setFlush
parameter_list|(
name|boolean
name|flush
parameter_list|)
block|{
name|this
operator|.
name|flush
operator|=
name|flush
expr_stmt|;
block|}
comment|/**      * @openwire:property version=2      * @return the start      */
specifier|public
name|boolean
name|isStart
parameter_list|()
block|{
return|return
name|this
operator|.
name|start
return|;
block|}
comment|/**      * @param start the start to set      */
specifier|public
name|void
name|setStart
parameter_list|(
name|boolean
name|start
parameter_list|)
block|{
name|this
operator|.
name|start
operator|=
name|start
expr_stmt|;
block|}
comment|/**      * @openwire:property version=2      * @return the stop      */
specifier|public
name|boolean
name|isStop
parameter_list|()
block|{
return|return
name|this
operator|.
name|stop
return|;
block|}
comment|/**      * @param stop the stop to set      */
specifier|public
name|void
name|setStop
parameter_list|(
name|boolean
name|stop
parameter_list|)
block|{
name|this
operator|.
name|stop
operator|=
name|stop
expr_stmt|;
block|}
block|}
end_class

end_unit


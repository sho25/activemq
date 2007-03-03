begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *   * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE  * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file  * to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the  * License. You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the  * specific language governing permissions and limitations under the License.  */
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
name|Destination
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
name|broker
operator|.
name|region
operator|.
name|Region
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
name|state
operator|.
name|ProducerState
import|;
end_import

begin_comment
comment|/**  * Holds internal state in the broker for a MessageProducer  *   * @version $Revision: 1.8 $  */
end_comment

begin_class
specifier|public
class|class
name|ProducerBrokerExchange
block|{
specifier|private
name|ConnectionContext
name|connectionContext
decl_stmt|;
specifier|private
name|Destination
name|regionDestination
decl_stmt|;
specifier|private
name|Region
name|region
decl_stmt|;
specifier|private
name|ProducerState
name|producerState
decl_stmt|;
specifier|private
name|boolean
name|mutable
init|=
literal|true
decl_stmt|;
comment|/**      * @return the connectionContext      */
specifier|public
name|ConnectionContext
name|getConnectionContext
parameter_list|()
block|{
return|return
name|this
operator|.
name|connectionContext
return|;
block|}
comment|/**      * @param connectionContext the connectionContext to set      */
specifier|public
name|void
name|setConnectionContext
parameter_list|(
name|ConnectionContext
name|connectionContext
parameter_list|)
block|{
name|this
operator|.
name|connectionContext
operator|=
name|connectionContext
expr_stmt|;
block|}
comment|/**      * @return the mutable      */
specifier|public
name|boolean
name|isMutable
parameter_list|()
block|{
return|return
name|this
operator|.
name|mutable
return|;
block|}
comment|/**      * @param mutable the mutable to set      */
specifier|public
name|void
name|setMutable
parameter_list|(
name|boolean
name|mutable
parameter_list|)
block|{
name|this
operator|.
name|mutable
operator|=
name|mutable
expr_stmt|;
block|}
comment|/**      * @return the regionDestination      */
specifier|public
name|Destination
name|getRegionDestination
parameter_list|()
block|{
return|return
name|this
operator|.
name|regionDestination
return|;
block|}
comment|/**      * @param regionDestination the regionDestination to set      */
specifier|public
name|void
name|setRegionDestination
parameter_list|(
name|Destination
name|regionDestination
parameter_list|)
block|{
name|this
operator|.
name|regionDestination
operator|=
name|regionDestination
expr_stmt|;
block|}
comment|/**      * @return the region      */
specifier|public
name|Region
name|getRegion
parameter_list|()
block|{
return|return
name|this
operator|.
name|region
return|;
block|}
comment|/**      * @param region the region to set      */
specifier|public
name|void
name|setRegion
parameter_list|(
name|Region
name|region
parameter_list|)
block|{
name|this
operator|.
name|region
operator|=
name|region
expr_stmt|;
block|}
comment|/**      * @return the producerState      */
specifier|public
name|ProducerState
name|getProducerState
parameter_list|()
block|{
return|return
name|this
operator|.
name|producerState
return|;
block|}
comment|/**      * @param producerState the producerState to set      */
specifier|public
name|void
name|setProducerState
parameter_list|(
name|ProducerState
name|producerState
parameter_list|)
block|{
name|this
operator|.
name|producerState
operator|=
name|producerState
expr_stmt|;
block|}
block|}
end_class

end_unit


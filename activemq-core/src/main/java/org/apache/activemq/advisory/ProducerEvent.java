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
name|advisory
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
name|command
operator|.
name|ProducerId
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Destination
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EventObject
import|;
end_import

begin_comment
comment|/**  * An event when the number of producers on a given destination changes.  *   * @version $Revision: 359679 $  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|ProducerEvent
extends|extends
name|EventObject
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|2442156576867593780L
decl_stmt|;
specifier|private
specifier|final
name|Destination
name|destination
decl_stmt|;
specifier|private
specifier|final
name|ProducerId
name|producerId
decl_stmt|;
specifier|private
specifier|final
name|int
name|producerCount
decl_stmt|;
specifier|public
name|ProducerEvent
parameter_list|(
name|ProducerEventSource
name|source
parameter_list|,
name|Destination
name|destination
parameter_list|,
name|ProducerId
name|producerId
parameter_list|,
name|int
name|producerCount
parameter_list|)
block|{
name|super
argument_list|(
name|source
argument_list|)
expr_stmt|;
name|this
operator|.
name|destination
operator|=
name|destination
expr_stmt|;
name|this
operator|.
name|producerId
operator|=
name|producerId
expr_stmt|;
name|this
operator|.
name|producerCount
operator|=
name|producerCount
expr_stmt|;
block|}
specifier|public
name|ProducerEventSource
name|getAdvisor
parameter_list|()
block|{
return|return
operator|(
name|ProducerEventSource
operator|)
name|getSource
argument_list|()
return|;
block|}
specifier|public
name|Destination
name|getDestination
parameter_list|()
block|{
return|return
name|destination
return|;
block|}
comment|/**      * Returns the current number of producers active at the time this advisory was sent.      *       */
specifier|public
name|int
name|getProducerCount
parameter_list|()
block|{
return|return
name|producerCount
return|;
block|}
specifier|public
name|ProducerId
name|getProducerId
parameter_list|()
block|{
return|return
name|producerId
return|;
block|}
specifier|public
specifier|abstract
name|boolean
name|isStarted
parameter_list|()
function_decl|;
block|}
end_class

end_unit


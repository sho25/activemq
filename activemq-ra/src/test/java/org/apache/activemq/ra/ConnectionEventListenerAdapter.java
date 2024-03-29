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
name|ra
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|resource
operator|.
name|spi
operator|.
name|ConnectionEvent
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|resource
operator|.
name|spi
operator|.
name|ConnectionEventListener
import|;
end_import

begin_class
specifier|public
class|class
name|ConnectionEventListenerAdapter
implements|implements
name|ConnectionEventListener
block|{
comment|/**      * @see javax.resource.spi.ConnectionEventListener#connectionClosed(javax.resource.spi.ConnectionEvent)      */
annotation|@
name|Override
specifier|public
name|void
name|connectionClosed
parameter_list|(
name|ConnectionEvent
name|arg0
parameter_list|)
block|{     }
comment|/**      * @see javax.resource.spi.ConnectionEventListener#localTransactionStarted(javax.resource.spi.ConnectionEvent)      */
annotation|@
name|Override
specifier|public
name|void
name|localTransactionStarted
parameter_list|(
name|ConnectionEvent
name|arg0
parameter_list|)
block|{     }
comment|/**      * @see javax.resource.spi.ConnectionEventListener#localTransactionCommitted(javax.resource.spi.ConnectionEvent)      */
annotation|@
name|Override
specifier|public
name|void
name|localTransactionCommitted
parameter_list|(
name|ConnectionEvent
name|arg0
parameter_list|)
block|{     }
comment|/**      * @see javax.resource.spi.ConnectionEventListener#localTransactionRolledback(javax.resource.spi.ConnectionEvent)      */
annotation|@
name|Override
specifier|public
name|void
name|localTransactionRolledback
parameter_list|(
name|ConnectionEvent
name|arg0
parameter_list|)
block|{     }
comment|/**      * @see javax.resource.spi.ConnectionEventListener#connectionErrorOccurred(javax.resource.spi.ConnectionEvent)      */
annotation|@
name|Override
specifier|public
name|void
name|connectionErrorOccurred
parameter_list|(
name|ConnectionEvent
name|arg0
parameter_list|)
block|{     }
block|}
end_class

end_unit


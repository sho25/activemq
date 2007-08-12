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

begin_comment
comment|/**  * A helper class used to provide access to the current active  * {@link InboundContext} instance being used to process a message in the  * current thread so that messages can be produced using the same session.  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|InboundContextSupport
block|{
specifier|private
specifier|static
specifier|final
name|ThreadLocal
argument_list|<
name|InboundContext
argument_list|>
name|THREAD_LOCAL
init|=
operator|new
name|ThreadLocal
argument_list|<
name|InboundContext
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|InboundContextSupport
parameter_list|()
block|{     }
comment|/**      * Returns the current {@link InboundContext} used by the current thread      * which is processing a message. This allows us to access the current      * Session to send a message using the same underlying session to avoid      * unnecessary XA or to use regular JMS transactions while using message      * driven POJOs.      *       * @return      */
specifier|public
specifier|static
name|InboundContext
name|getActiveSessionAndProducer
parameter_list|()
block|{
return|return
name|THREAD_LOCAL
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**      * Registers the session and producer which should be called before the      * {@link javax.resource.spi.endpoint.MessageEndpoint#beforeDelivery(java.lang.reflect.Method)}      * method is called.      *       * @param sessionAndProducer      */
specifier|public
specifier|static
name|void
name|register
parameter_list|(
name|InboundContext
name|sessionAndProducer
parameter_list|)
block|{
name|THREAD_LOCAL
operator|.
name|set
argument_list|(
name|sessionAndProducer
argument_list|)
expr_stmt|;
block|}
comment|/**      * Unregisters the session and producer which should be called after the      * {@link javax.resource.spi.endpoint.MessageEndpoint#afterDelivery()}      * method is called.      *       * @param sessionAndProducer      */
specifier|public
specifier|static
name|void
name|unregister
parameter_list|(
name|InboundContext
name|sessionAndProducer
parameter_list|)
block|{
name|THREAD_LOCAL
operator|.
name|set
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit


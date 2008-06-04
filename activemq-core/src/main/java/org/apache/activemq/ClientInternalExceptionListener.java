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
package|;
end_package

begin_comment
comment|/**  * An exception listener similar to the standard<code>javax.jms.ExceptionListener</code>  * which can be used by client code to be notified of exceptions thrown by container components   * (e.g. an EJB container in case of Message Driven Beans) during asynchronous processing of a message.  *<p>  * The<code>org.apache.activemq.ActiveMQConnection</code> that the listener has been registered with does  * this by calling the listener's<code>onException()</code> method passing it a<code>Throwable</code> describing  * the problem.  *</p>  *   * @author Kai Hudalla  * @see ActiveMQConnection#setClientInternalExceptionListener(org.apache.activemq.ClientInternalExceptionListener)  */
end_comment

begin_interface
specifier|public
interface|interface
name|ClientInternalExceptionListener
block|{
comment|/**      * Notifies a client of an exception while asynchronously processing a message.      *       * @param exception the exception describing the problem      */
name|void
name|onException
parameter_list|(
name|Throwable
name|exception
parameter_list|)
function_decl|;
block|}
end_interface

end_unit


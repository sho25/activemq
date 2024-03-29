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

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|JMSException
import|;
end_import

begin_comment
comment|/**  * Provides a uniform interface that can be used to close all the JMS obejcts  * that provide a close() method. Useful for when you want to collect a  * heterogeous set of JMS object in a collection to be closed at a later time.  *   *   */
end_comment

begin_interface
specifier|public
interface|interface
name|Closeable
block|{
comment|/**      * Closes a JMS object.      *<P>      * Many JMS objects are closeable such as Connections, Sessions, Consumers      * and Producers.      *       * @throws JMSException if the JMS provider fails to close the object due to      *                 some internal error.      */
name|void
name|close
parameter_list|()
throws|throws
name|JMSException
function_decl|;
block|}
end_interface

end_unit


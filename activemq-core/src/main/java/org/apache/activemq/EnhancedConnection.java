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
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TopicConnection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|QueueConnection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|JMSException
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
name|advisory
operator|.
name|DestinationSource
import|;
end_import

begin_comment
comment|/**  * A set of enhanced APIs for a JMS provider  *  * @version $Revision: 1.1 $  */
end_comment

begin_interface
specifier|public
interface|interface
name|EnhancedConnection
extends|extends
name|TopicConnection
extends|,
name|QueueConnection
extends|,
name|Closeable
block|{
comment|/**      * Returns the {@link DestinationSource} object which can be used to listen to destinations      * being created or destroyed or to enquire about the current destinations available on the broker      *      * @return a lazily created destination source      * @throws JMSException      */
name|DestinationSource
name|getDestinationSource
parameter_list|()
throws|throws
name|JMSException
function_decl|;
block|}
end_interface

end_unit


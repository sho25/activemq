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
operator|.
name|camel
operator|.
name|converter
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
name|ActiveMQDestination
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|Converter
import|;
end_import

begin_comment
comment|/**  * @version $Revision$  */
end_comment

begin_class
annotation|@
name|Converter
specifier|public
class|class
name|ActiveMQConverter
block|{
comment|/**      * Converts a URL in ActiveMQ syntax to a destination such as to support      * "queue://foo.bar" or 'topic://bar.whatnot". Things default to queues if no scheme.      *      * This allows ActiveMQ destinations to be passed around as Strings and converted back again.      *      * @param name is the name of the queue or the full URI using prefixes queue:// or topic://      * @return the ActiveMQ destination      */
annotation|@
name|Converter
specifier|public
specifier|static
name|ActiveMQDestination
name|toDestination
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|ActiveMQDestination
operator|.
name|createDestination
argument_list|(
name|name
argument_list|,
name|ActiveMQDestination
operator|.
name|QUEUE_TYPE
argument_list|)
return|;
block|}
block|}
end_class

end_unit


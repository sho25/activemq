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
name|systest
package|;
end_package

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
name|javax
operator|.
name|jms
operator|.
name|JMSException
import|;
end_import

begin_comment
comment|/**  * A factory to create destinations which works in any version of any JMS provider  *   * @version $Revision: 1.1 $  */
end_comment

begin_interface
specifier|public
interface|interface
name|DestinationFactory
block|{
specifier|public
name|Destination
name|createDestination
parameter_list|(
name|String
name|physicalName
parameter_list|,
name|int
name|destinationType
parameter_list|)
throws|throws
name|JMSException
function_decl|;
specifier|public
specifier|static
specifier|final
name|int
name|TOPIC
init|=
literal|1
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|QUEUE
init|=
literal|2
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|TEMPORARY_TOPIC
init|=
literal|3
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|TEMPORARY_QUEUE
init|=
literal|4
decl_stmt|;
block|}
end_interface

end_unit


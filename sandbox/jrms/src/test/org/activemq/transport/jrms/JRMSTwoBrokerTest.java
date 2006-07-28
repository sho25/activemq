begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|activemq
operator|.
name|transport
operator|.
name|jrms
package|;
end_package

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|transport
operator|.
name|multicast
operator|.
name|MulticastTwoBrokerTest
import|;
end_import

begin_comment
comment|/**  * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|JRMSTwoBrokerTest
extends|extends
name|MulticastTwoBrokerTest
block|{
specifier|protected
name|String
name|getBrokerURL
parameter_list|()
block|{
return|return
literal|"jrms://228.5.6.7:6677"
return|;
block|}
block|}
end_class

end_unit


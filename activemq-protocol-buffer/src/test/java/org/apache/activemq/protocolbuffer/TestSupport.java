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
name|protocolbuffer
package|;
end_package

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_comment
comment|/**  * @version $Revision: 1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|TestSupport
extends|extends
name|TestCase
block|{
comment|// TODO seems like 4m messages cause Protocol Buffers to barf but 3m is fine :)
specifier|protected
name|long
name|messageCount
init|=
literal|4
operator|*
literal|1000
operator|*
literal|1000
decl_stmt|;
specifier|protected
name|boolean
name|verbose
init|=
literal|false
decl_stmt|;
specifier|protected
name|boolean
name|doAssertions
init|=
literal|true
decl_stmt|;
specifier|protected
name|boolean
name|useProducerId
init|=
literal|false
decl_stmt|;
specifier|protected
name|StopWatch
name|createStopWatch
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|StopWatch
name|answer
init|=
operator|new
name|StopWatch
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|answer
operator|.
name|setLogFrequency
argument_list|(
operator|(
name|int
operator|)
name|messageCount
operator|/
literal|10
argument_list|)
expr_stmt|;
return|return
name|answer
return|;
block|}
block|}
end_class

end_unit


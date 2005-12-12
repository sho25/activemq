begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** *<a href="http://activemq.org">ActiveMQ: The Open Source Message Fabric</a> * * Copyright 2005 (C) LogicBlaze, Inc. http://www.logicblaze.com * * Licensed under the Apache License, Version 2.0 (the "License"); * you may not use this file except in compliance with the License. * You may obtain a copy of the License at * * http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. * **/
end_comment

begin_package
package|package
name|org
operator|.
name|activemq
operator|.
name|broker
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activeio
operator|.
name|command
operator|.
name|WireFormat
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|command
operator|.
name|Command
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|command
operator|.
name|Response
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|openwire
operator|.
name|OpenWireFormat
import|;
end_import

begin_comment
comment|/**  * Runs against the broker but marshals all request and response commands.  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|MarshallingBrokerTest
extends|extends
name|BrokerTest
block|{
specifier|public
name|WireFormat
name|wireFormat
init|=
operator|new
name|OpenWireFormat
argument_list|()
decl_stmt|;
specifier|public
name|void
name|initCombos
parameter_list|()
block|{
name|addCombinationValues
argument_list|(
literal|"wireFormat"
argument_list|,
operator|new
name|Object
index|[]
block|{
operator|new
name|OpenWireFormat
argument_list|(
literal|true
argument_list|)
block|,
operator|new
name|OpenWireFormat
argument_list|(
literal|false
argument_list|)
block|,                 }
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|StubConnection
name|createConnection
parameter_list|()
throws|throws
name|Exception
block|{
return|return
operator|new
name|StubConnection
argument_list|(
name|broker
argument_list|)
block|{
specifier|public
name|Response
name|request
parameter_list|(
name|Command
name|command
parameter_list|)
throws|throws
name|Throwable
block|{
name|Response
name|r
init|=
name|super
operator|.
name|request
argument_list|(
operator|(
name|Command
operator|)
name|wireFormat
operator|.
name|unmarshal
argument_list|(
name|wireFormat
operator|.
name|marshal
argument_list|(
name|command
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|r
operator|!=
literal|null
condition|)
block|{
name|r
operator|=
operator|(
name|Response
operator|)
name|wireFormat
operator|.
name|unmarshal
argument_list|(
name|wireFormat
operator|.
name|marshal
argument_list|(
name|r
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|r
return|;
block|}
specifier|public
name|void
name|send
parameter_list|(
name|Command
name|command
parameter_list|)
throws|throws
name|Throwable
block|{
name|super
operator|.
name|send
argument_list|(
operator|(
name|Command
operator|)
name|wireFormat
operator|.
name|unmarshal
argument_list|(
name|wireFormat
operator|.
name|marshal
argument_list|(
name|command
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|dispatch
parameter_list|(
name|Command
name|command
parameter_list|)
throws|throws
name|InterruptedException
throws|,
name|IOException
block|{
name|super
operator|.
name|dispatch
argument_list|(
operator|(
name|Command
operator|)
name|wireFormat
operator|.
name|unmarshal
argument_list|(
name|wireFormat
operator|.
name|marshal
argument_list|(
name|command
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
empty_stmt|;
block|}
return|;
block|}
specifier|public
specifier|static
name|Test
name|suite
parameter_list|()
block|{
return|return
name|suite
argument_list|(
name|MarshallingBrokerTest
operator|.
name|class
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|junit
operator|.
name|textui
operator|.
name|TestRunner
operator|.
name|run
argument_list|(
name|suite
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit


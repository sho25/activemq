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
name|openwire
package|;
end_package

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
name|activeio
operator|.
name|command
operator|.
name|WireFormatFactory
import|;
end_import

begin_comment
comment|/**  * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|OpenWireFormatFactory
implements|implements
name|WireFormatFactory
block|{
specifier|private
name|int
name|version
init|=
literal|1
decl_stmt|;
specifier|private
name|boolean
name|stackTraceEnabled
init|=
literal|true
decl_stmt|;
specifier|private
name|boolean
name|tcpNoDelayEnabled
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|cacheEnabled
init|=
literal|true
decl_stmt|;
specifier|public
name|WireFormat
name|createWireFormat
parameter_list|()
block|{
name|OpenWireFormat
name|format
init|=
operator|new
name|OpenWireFormat
argument_list|()
decl_stmt|;
name|format
operator|.
name|setVersion
argument_list|(
name|version
argument_list|)
expr_stmt|;
name|format
operator|.
name|setStackTraceEnabled
argument_list|(
name|stackTraceEnabled
argument_list|)
expr_stmt|;
name|format
operator|.
name|setCacheEnabled
argument_list|(
name|cacheEnabled
argument_list|)
expr_stmt|;
name|format
operator|.
name|setTcpNoDelayEnabled
argument_list|(
name|tcpNoDelayEnabled
argument_list|)
expr_stmt|;
return|return
name|format
return|;
block|}
specifier|public
name|boolean
name|isStackTraceEnabled
parameter_list|()
block|{
return|return
name|stackTraceEnabled
return|;
block|}
specifier|public
name|void
name|setStackTraceEnabled
parameter_list|(
name|boolean
name|stackTraceEnabled
parameter_list|)
block|{
name|this
operator|.
name|stackTraceEnabled
operator|=
name|stackTraceEnabled
expr_stmt|;
block|}
specifier|public
name|boolean
name|isTcpNoDelayEnabled
parameter_list|()
block|{
return|return
name|tcpNoDelayEnabled
return|;
block|}
specifier|public
name|void
name|setTcpNoDelayEnabled
parameter_list|(
name|boolean
name|tcpNoDelayEnabled
parameter_list|)
block|{
name|this
operator|.
name|tcpNoDelayEnabled
operator|=
name|tcpNoDelayEnabled
expr_stmt|;
block|}
specifier|public
name|int
name|getVersion
parameter_list|()
block|{
return|return
name|version
return|;
block|}
specifier|public
name|void
name|setVersion
parameter_list|(
name|int
name|version
parameter_list|)
block|{
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
block|}
specifier|public
name|boolean
name|isCacheEnabled
parameter_list|()
block|{
return|return
name|cacheEnabled
return|;
block|}
specifier|public
name|void
name|setCacheEnabled
parameter_list|(
name|boolean
name|cacheEnabled
parameter_list|)
block|{
name|this
operator|.
name|cacheEnabled
operator|=
name|cacheEnabled
expr_stmt|;
block|}
block|}
end_class

end_unit


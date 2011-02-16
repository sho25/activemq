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
name|openwire
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
name|WireFormatInfo
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
name|wireformat
operator|.
name|WireFormat
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
name|wireformat
operator|.
name|WireFormatFactory
import|;
end_import

begin_comment
comment|/**  *   */
end_comment

begin_class
specifier|public
class|class
name|OpenWireFormatFactory
implements|implements
name|WireFormatFactory
block|{
comment|//
comment|// The default values here are what the wire format changes to after a
comment|// default negotiation.
comment|//
specifier|private
name|int
name|version
init|=
name|OpenWireFormat
operator|.
name|DEFAULT_VERSION
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
literal|true
decl_stmt|;
specifier|private
name|boolean
name|cacheEnabled
init|=
literal|true
decl_stmt|;
specifier|private
name|boolean
name|tightEncodingEnabled
init|=
literal|true
decl_stmt|;
specifier|private
name|boolean
name|sizePrefixDisabled
decl_stmt|;
specifier|private
name|long
name|maxInactivityDuration
init|=
literal|30
operator|*
literal|1000
decl_stmt|;
specifier|private
name|long
name|maxInactivityDurationInitalDelay
init|=
literal|10
operator|*
literal|1000
decl_stmt|;
specifier|private
name|int
name|cacheSize
init|=
literal|1024
decl_stmt|;
specifier|public
name|WireFormat
name|createWireFormat
parameter_list|()
block|{
name|WireFormatInfo
name|info
init|=
operator|new
name|WireFormatInfo
argument_list|()
decl_stmt|;
name|info
operator|.
name|setVersion
argument_list|(
name|version
argument_list|)
expr_stmt|;
try|try
block|{
name|info
operator|.
name|setStackTraceEnabled
argument_list|(
name|stackTraceEnabled
argument_list|)
expr_stmt|;
name|info
operator|.
name|setCacheEnabled
argument_list|(
name|cacheEnabled
argument_list|)
expr_stmt|;
name|info
operator|.
name|setTcpNoDelayEnabled
argument_list|(
name|tcpNoDelayEnabled
argument_list|)
expr_stmt|;
name|info
operator|.
name|setTightEncodingEnabled
argument_list|(
name|tightEncodingEnabled
argument_list|)
expr_stmt|;
name|info
operator|.
name|setSizePrefixDisabled
argument_list|(
name|sizePrefixDisabled
argument_list|)
expr_stmt|;
name|info
operator|.
name|setMaxInactivityDuration
argument_list|(
name|maxInactivityDuration
argument_list|)
expr_stmt|;
name|info
operator|.
name|setMaxInactivityDurationInitalDelay
argument_list|(
name|maxInactivityDurationInitalDelay
argument_list|)
expr_stmt|;
name|info
operator|.
name|setCacheSize
argument_list|(
name|cacheSize
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|IllegalStateException
name|ise
init|=
operator|new
name|IllegalStateException
argument_list|(
literal|"Could not configure WireFormatInfo"
argument_list|)
decl_stmt|;
name|ise
operator|.
name|initCause
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
name|ise
throw|;
block|}
name|OpenWireFormat
name|f
init|=
operator|new
name|OpenWireFormat
argument_list|()
decl_stmt|;
name|f
operator|.
name|setPreferedWireFormatInfo
argument_list|(
name|info
argument_list|)
expr_stmt|;
return|return
name|f
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
specifier|public
name|boolean
name|isTightEncodingEnabled
parameter_list|()
block|{
return|return
name|tightEncodingEnabled
return|;
block|}
specifier|public
name|void
name|setTightEncodingEnabled
parameter_list|(
name|boolean
name|tightEncodingEnabled
parameter_list|)
block|{
name|this
operator|.
name|tightEncodingEnabled
operator|=
name|tightEncodingEnabled
expr_stmt|;
block|}
specifier|public
name|boolean
name|isSizePrefixDisabled
parameter_list|()
block|{
return|return
name|sizePrefixDisabled
return|;
block|}
specifier|public
name|void
name|setSizePrefixDisabled
parameter_list|(
name|boolean
name|sizePrefixDisabled
parameter_list|)
block|{
name|this
operator|.
name|sizePrefixDisabled
operator|=
name|sizePrefixDisabled
expr_stmt|;
block|}
specifier|public
name|long
name|getMaxInactivityDuration
parameter_list|()
block|{
return|return
name|maxInactivityDuration
return|;
block|}
specifier|public
name|void
name|setMaxInactivityDuration
parameter_list|(
name|long
name|maxInactivityDuration
parameter_list|)
block|{
name|this
operator|.
name|maxInactivityDuration
operator|=
name|maxInactivityDuration
expr_stmt|;
block|}
specifier|public
name|int
name|getCacheSize
parameter_list|()
block|{
return|return
name|cacheSize
return|;
block|}
specifier|public
name|void
name|setCacheSize
parameter_list|(
name|int
name|cacheSize
parameter_list|)
block|{
name|this
operator|.
name|cacheSize
operator|=
name|cacheSize
expr_stmt|;
block|}
specifier|public
name|long
name|getMaxInactivityDurationInitalDelay
parameter_list|()
block|{
return|return
name|maxInactivityDurationInitalDelay
return|;
block|}
specifier|public
name|void
name|setMaxInactivityDurationInitalDelay
parameter_list|(
name|long
name|maxInactivityDurationInitalDelay
parameter_list|)
block|{
name|this
operator|.
name|maxInactivityDurationInitalDelay
operator|=
name|maxInactivityDurationInitalDelay
expr_stmt|;
block|}
block|}
end_class

end_unit


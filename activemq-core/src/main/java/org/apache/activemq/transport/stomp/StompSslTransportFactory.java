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
name|transport
operator|.
name|stomp
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
name|transport
operator|.
name|tcp
operator|.
name|TcpTransportFactory
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
name|transport
operator|.
name|tcp
operator|.
name|SslTransportFactory
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
name|transport
operator|.
name|Transport
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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * A<a href="http://stomp.codehaus.org/">STOMP</a> over SSL transport factory  *  * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|StompSslTransportFactory
extends|extends
name|SslTransportFactory
block|{
specifier|protected
name|String
name|getDefaultWireFormatType
parameter_list|()
block|{
return|return
literal|"stomp"
return|;
block|}
specifier|public
name|Transport
name|compositeConfigure
parameter_list|(
name|Transport
name|transport
parameter_list|,
name|WireFormat
name|format
parameter_list|,
name|Map
name|options
parameter_list|)
block|{
name|transport
operator|=
operator|new
name|StompTransportFilter
argument_list|(
name|transport
argument_list|,
operator|new
name|LegacyFrameTranslator
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|compositeConfigure
argument_list|(
name|transport
argument_list|,
name|format
argument_list|,
name|options
argument_list|)
return|;
block|}
block|}
end_class

end_unit


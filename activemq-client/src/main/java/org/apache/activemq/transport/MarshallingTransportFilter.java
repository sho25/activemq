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
name|transport
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
name|org
operator|.
name|apache
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
name|apache
operator|.
name|activemq
operator|.
name|wireformat
operator|.
name|WireFormat
import|;
end_import

begin_class
specifier|public
class|class
name|MarshallingTransportFilter
extends|extends
name|TransportFilter
block|{
specifier|private
specifier|final
name|WireFormat
name|localWireFormat
decl_stmt|;
specifier|private
specifier|final
name|WireFormat
name|remoteWireFormat
decl_stmt|;
specifier|public
name|MarshallingTransportFilter
parameter_list|(
name|Transport
name|next
parameter_list|,
name|WireFormat
name|localWireFormat
parameter_list|,
name|WireFormat
name|remoteWireFormat
parameter_list|)
block|{
name|super
argument_list|(
name|next
argument_list|)
expr_stmt|;
name|this
operator|.
name|localWireFormat
operator|=
name|localWireFormat
expr_stmt|;
name|this
operator|.
name|remoteWireFormat
operator|=
name|remoteWireFormat
expr_stmt|;
block|}
specifier|public
name|void
name|oneway
parameter_list|(
name|Object
name|command
parameter_list|)
throws|throws
name|IOException
block|{
name|next
operator|.
name|oneway
argument_list|(
operator|(
name|Command
operator|)
name|remoteWireFormat
operator|.
name|unmarshal
argument_list|(
name|localWireFormat
operator|.
name|marshal
argument_list|(
name|command
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|onCommand
parameter_list|(
name|Object
name|command
parameter_list|)
block|{
try|try
block|{
name|getTransportListener
argument_list|()
operator|.
name|onCommand
argument_list|(
operator|(
name|Command
operator|)
name|localWireFormat
operator|.
name|unmarshal
argument_list|(
name|remoteWireFormat
operator|.
name|marshal
argument_list|(
name|command
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|getTransportListener
argument_list|()
operator|.
name|onException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit


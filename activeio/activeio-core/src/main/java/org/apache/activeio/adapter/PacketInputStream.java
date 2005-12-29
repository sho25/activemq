begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activeio
operator|.
name|adapter
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activeio
operator|.
name|packet
operator|.
name|Packet
import|;
end_import

begin_comment
comment|/**  * @deprecated  Use PacketToInputStream instead.  This class will be removed very soon.  */
end_comment

begin_class
specifier|public
class|class
name|PacketInputStream
extends|extends
name|PacketToInputStream
block|{
specifier|public
name|PacketInputStream
parameter_list|(
name|Packet
name|packet
parameter_list|)
block|{
name|super
argument_list|(
name|packet
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit


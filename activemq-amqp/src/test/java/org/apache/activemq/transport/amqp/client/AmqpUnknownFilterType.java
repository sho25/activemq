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
operator|.
name|amqp
operator|.
name|client
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|qpid
operator|.
name|proton
operator|.
name|amqp
operator|.
name|DescribedType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|qpid
operator|.
name|proton
operator|.
name|amqp
operator|.
name|Symbol
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|qpid
operator|.
name|proton
operator|.
name|amqp
operator|.
name|UnsignedLong
import|;
end_import

begin_comment
comment|/**  * A Described Type wrapper for an unsupported filter that the broker should ignore.  */
end_comment

begin_class
specifier|public
class|class
name|AmqpUnknownFilterType
implements|implements
name|DescribedType
block|{
specifier|public
specifier|static
specifier|final
name|AmqpUnknownFilterType
name|UNKOWN_FILTER
init|=
operator|new
name|AmqpUnknownFilterType
argument_list|()
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|UnsignedLong
name|UNKNOWN_FILTER_CODE
init|=
name|UnsignedLong
operator|.
name|valueOf
argument_list|(
literal|0x0000468C00000099L
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|Symbol
name|UNKNOWN_FILTER_NAME
init|=
name|Symbol
operator|.
name|valueOf
argument_list|(
literal|"apache.org:unkown-filter:string"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|Object
index|[]
name|UNKNOWN_FILTER_IDS
init|=
operator|new
name|Object
index|[]
block|{
name|UNKNOWN_FILTER_CODE
block|,
name|UNKNOWN_FILTER_NAME
block|}
decl_stmt|;
specifier|private
specifier|final
name|String
name|payload
decl_stmt|;
specifier|public
name|AmqpUnknownFilterType
parameter_list|()
block|{
name|this
operator|.
name|payload
operator|=
literal|"UnknownFilter{}"
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|getDescriptor
parameter_list|()
block|{
return|return
name|UNKNOWN_FILTER_CODE
return|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|getDescribed
parameter_list|()
block|{
return|return
name|this
operator|.
name|payload
return|;
block|}
block|}
end_class

end_unit

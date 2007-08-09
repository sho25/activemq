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
operator|.
name|v1
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutput
import|;
end_import

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
name|DataStructure
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
name|ProducerInfo
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
name|openwire
operator|.
name|BooleanStream
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
name|openwire
operator|.
name|OpenWireFormat
import|;
end_import

begin_comment
comment|/**  * Marshalling code for Open Wire Format for ProducerInfoMarshaller NOTE!: This  * file is auto generated - do not modify! if you need to make a change, please  * see the modify the groovy scripts in the under src/gram/script and then use  * maven openwire:generate to regenerate this file.  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|ProducerInfoMarshaller
extends|extends
name|BaseCommandMarshaller
block|{
comment|/**      * Return the type of Data Structure we marshal      *       * @return short representation of the type data structure      */
specifier|public
name|byte
name|getDataStructureType
parameter_list|()
block|{
return|return
name|ProducerInfo
operator|.
name|DATA_STRUCTURE_TYPE
return|;
block|}
comment|/**      * @return a new object instance      */
specifier|public
name|DataStructure
name|createObject
parameter_list|()
block|{
return|return
operator|new
name|ProducerInfo
argument_list|()
return|;
block|}
comment|/**      * Un-marshal an object instance from the data input stream      *       * @param o the object to un-marshal      * @param dataIn the data input stream to build the object from      * @throws IOException      */
specifier|public
name|void
name|tightUnmarshal
parameter_list|(
name|OpenWireFormat
name|wireFormat
parameter_list|,
name|Object
name|o
parameter_list|,
name|DataInput
name|dataIn
parameter_list|,
name|BooleanStream
name|bs
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|tightUnmarshal
argument_list|(
name|wireFormat
argument_list|,
name|o
argument_list|,
name|dataIn
argument_list|,
name|bs
argument_list|)
expr_stmt|;
name|ProducerInfo
name|info
init|=
operator|(
name|ProducerInfo
operator|)
name|o
decl_stmt|;
name|info
operator|.
name|setProducerId
argument_list|(
operator|(
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|ProducerId
operator|)
name|tightUnmarsalCachedObject
argument_list|(
name|wireFormat
argument_list|,
name|dataIn
argument_list|,
name|bs
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|setDestination
argument_list|(
operator|(
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|ActiveMQDestination
operator|)
name|tightUnmarsalCachedObject
argument_list|(
name|wireFormat
argument_list|,
name|dataIn
argument_list|,
name|bs
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|bs
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|short
name|size
init|=
name|dataIn
operator|.
name|readShort
argument_list|()
decl_stmt|;
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|BrokerId
name|value
index|[]
init|=
operator|new
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|BrokerId
index|[
name|size
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|size
condition|;
name|i
operator|++
control|)
block|{
name|value
index|[
name|i
index|]
operator|=
operator|(
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|BrokerId
operator|)
name|tightUnmarsalNestedObject
argument_list|(
name|wireFormat
argument_list|,
name|dataIn
argument_list|,
name|bs
argument_list|)
expr_stmt|;
block|}
name|info
operator|.
name|setBrokerPath
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|info
operator|.
name|setBrokerPath
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Write the booleans that this object uses to a BooleanStream      */
specifier|public
name|int
name|tightMarshal1
parameter_list|(
name|OpenWireFormat
name|wireFormat
parameter_list|,
name|Object
name|o
parameter_list|,
name|BooleanStream
name|bs
parameter_list|)
throws|throws
name|IOException
block|{
name|ProducerInfo
name|info
init|=
operator|(
name|ProducerInfo
operator|)
name|o
decl_stmt|;
name|int
name|rc
init|=
name|super
operator|.
name|tightMarshal1
argument_list|(
name|wireFormat
argument_list|,
name|o
argument_list|,
name|bs
argument_list|)
decl_stmt|;
name|rc
operator|+=
name|tightMarshalCachedObject1
argument_list|(
name|wireFormat
argument_list|,
operator|(
name|DataStructure
operator|)
name|info
operator|.
name|getProducerId
argument_list|()
argument_list|,
name|bs
argument_list|)
expr_stmt|;
name|rc
operator|+=
name|tightMarshalCachedObject1
argument_list|(
name|wireFormat
argument_list|,
operator|(
name|DataStructure
operator|)
name|info
operator|.
name|getDestination
argument_list|()
argument_list|,
name|bs
argument_list|)
expr_stmt|;
name|rc
operator|+=
name|tightMarshalObjectArray1
argument_list|(
name|wireFormat
argument_list|,
name|info
operator|.
name|getBrokerPath
argument_list|()
argument_list|,
name|bs
argument_list|)
expr_stmt|;
return|return
name|rc
operator|+
literal|0
return|;
block|}
comment|/**      * Write a object instance to data output stream      *       * @param o the instance to be marshaled      * @param dataOut the output stream      * @throws IOException thrown if an error occurs      */
specifier|public
name|void
name|tightMarshal2
parameter_list|(
name|OpenWireFormat
name|wireFormat
parameter_list|,
name|Object
name|o
parameter_list|,
name|DataOutput
name|dataOut
parameter_list|,
name|BooleanStream
name|bs
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|tightMarshal2
argument_list|(
name|wireFormat
argument_list|,
name|o
argument_list|,
name|dataOut
argument_list|,
name|bs
argument_list|)
expr_stmt|;
name|ProducerInfo
name|info
init|=
operator|(
name|ProducerInfo
operator|)
name|o
decl_stmt|;
name|tightMarshalCachedObject2
argument_list|(
name|wireFormat
argument_list|,
operator|(
name|DataStructure
operator|)
name|info
operator|.
name|getProducerId
argument_list|()
argument_list|,
name|dataOut
argument_list|,
name|bs
argument_list|)
expr_stmt|;
name|tightMarshalCachedObject2
argument_list|(
name|wireFormat
argument_list|,
operator|(
name|DataStructure
operator|)
name|info
operator|.
name|getDestination
argument_list|()
argument_list|,
name|dataOut
argument_list|,
name|bs
argument_list|)
expr_stmt|;
name|tightMarshalObjectArray2
argument_list|(
name|wireFormat
argument_list|,
name|info
operator|.
name|getBrokerPath
argument_list|()
argument_list|,
name|dataOut
argument_list|,
name|bs
argument_list|)
expr_stmt|;
block|}
comment|/**      * Un-marshal an object instance from the data input stream      *       * @param o the object to un-marshal      * @param dataIn the data input stream to build the object from      * @throws IOException      */
specifier|public
name|void
name|looseUnmarshal
parameter_list|(
name|OpenWireFormat
name|wireFormat
parameter_list|,
name|Object
name|o
parameter_list|,
name|DataInput
name|dataIn
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|looseUnmarshal
argument_list|(
name|wireFormat
argument_list|,
name|o
argument_list|,
name|dataIn
argument_list|)
expr_stmt|;
name|ProducerInfo
name|info
init|=
operator|(
name|ProducerInfo
operator|)
name|o
decl_stmt|;
name|info
operator|.
name|setProducerId
argument_list|(
operator|(
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|ProducerId
operator|)
name|looseUnmarsalCachedObject
argument_list|(
name|wireFormat
argument_list|,
name|dataIn
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|setDestination
argument_list|(
operator|(
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|ActiveMQDestination
operator|)
name|looseUnmarsalCachedObject
argument_list|(
name|wireFormat
argument_list|,
name|dataIn
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|dataIn
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|short
name|size
init|=
name|dataIn
operator|.
name|readShort
argument_list|()
decl_stmt|;
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|BrokerId
name|value
index|[]
init|=
operator|new
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|BrokerId
index|[
name|size
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|size
condition|;
name|i
operator|++
control|)
block|{
name|value
index|[
name|i
index|]
operator|=
operator|(
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|BrokerId
operator|)
name|looseUnmarsalNestedObject
argument_list|(
name|wireFormat
argument_list|,
name|dataIn
argument_list|)
expr_stmt|;
block|}
name|info
operator|.
name|setBrokerPath
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|info
operator|.
name|setBrokerPath
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Write the booleans that this object uses to a BooleanStream      */
specifier|public
name|void
name|looseMarshal
parameter_list|(
name|OpenWireFormat
name|wireFormat
parameter_list|,
name|Object
name|o
parameter_list|,
name|DataOutput
name|dataOut
parameter_list|)
throws|throws
name|IOException
block|{
name|ProducerInfo
name|info
init|=
operator|(
name|ProducerInfo
operator|)
name|o
decl_stmt|;
name|super
operator|.
name|looseMarshal
argument_list|(
name|wireFormat
argument_list|,
name|o
argument_list|,
name|dataOut
argument_list|)
expr_stmt|;
name|looseMarshalCachedObject
argument_list|(
name|wireFormat
argument_list|,
operator|(
name|DataStructure
operator|)
name|info
operator|.
name|getProducerId
argument_list|()
argument_list|,
name|dataOut
argument_list|)
expr_stmt|;
name|looseMarshalCachedObject
argument_list|(
name|wireFormat
argument_list|,
operator|(
name|DataStructure
operator|)
name|info
operator|.
name|getDestination
argument_list|()
argument_list|,
name|dataOut
argument_list|)
expr_stmt|;
name|looseMarshalObjectArray
argument_list|(
name|wireFormat
argument_list|,
name|info
operator|.
name|getBrokerPath
argument_list|()
argument_list|,
name|dataOut
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit


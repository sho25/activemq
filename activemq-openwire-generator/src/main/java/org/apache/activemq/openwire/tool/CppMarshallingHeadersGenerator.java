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
name|openwire
operator|.
name|tool
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_comment
comment|/**  *  * @version $Revision: 381410 $  */
end_comment

begin_class
specifier|public
class|class
name|CppMarshallingHeadersGenerator
extends|extends
name|JavaMarshallingGenerator
block|{
specifier|protected
name|String
name|targetDir
init|=
literal|"./src"
decl_stmt|;
specifier|public
name|Object
name|run
parameter_list|()
block|{
name|filePostFix
operator|=
name|getFilePostFix
argument_list|()
expr_stmt|;
if|if
condition|(
name|destDir
operator|==
literal|null
condition|)
block|{
name|destDir
operator|=
operator|new
name|File
argument_list|(
name|targetDir
operator|+
literal|"/marshal"
argument_list|)
expr_stmt|;
block|}
return|return
name|super
operator|.
name|run
argument_list|()
return|;
block|}
specifier|protected
name|String
name|getFilePostFix
parameter_list|()
block|{
return|return
literal|".hpp"
return|;
block|}
specifier|protected
name|void
name|generateLicence
parameter_list|(
name|PrintWriter
name|out
parameter_list|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"/*"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|" *"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|" * Licensed to the Apache Software Foundation (ASF) under one or more"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|" * contributor license agreements.  See the NOTICE file distributed with"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|" * this work for additional information regarding copyright ownership."
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|" * The ASF licenses this file to You under the Apache License, Version 2.0"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|" * (the \"License\"); you may not use this file except in compliance with"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|" * the License.  You may obtain a copy of the License at"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|" *"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|" * http://www.apache.org/licenses/LICENSE-2.0"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|" *"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|" * Unless required by applicable law or agreed to in writing, software"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|" * distributed under the License is distributed on an \"AS IS\" BASIS,"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|" * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied."
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|" * See the License for the specific language governing permissions and"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|" * limitations under the License."
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|" */"
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|generateFile
parameter_list|(
name|PrintWriter
name|out
parameter_list|)
throws|throws
name|Exception
block|{
name|generateLicence
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"#ifndef "
operator|+
name|className
operator|+
literal|"_hpp_"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"#define "
operator|+
name|className
operator|+
literal|"_hpp_"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"#include<string>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"#include \"command/IDataStructure.hpp\""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"/* we could cut this down  - for now include all possible headers */"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"#include \"command/BrokerId.hpp\""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"#include \"command/ConnectionId.hpp\""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"#include \"command/ConsumerId.hpp\""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"#include \"command/ProducerId.hpp\""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"#include \"command/SessionId.hpp\""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"#include \"io/BinaryReader.hpp\""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"#include \"io/BinaryWriter.hpp\""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"#include \"command/"
operator|+
name|baseClass
operator|+
literal|".hpp\""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"#include \"util/ifr/p.hpp\""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"#include \"protocol/ProtocolFormat.hpp\""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"namespace apache"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"{"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"  namespace activemq"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"  {"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    namespace client"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    {"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"      namespace marshal"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"      {"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"        using namespace ifr ;"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"        using namespace apache::activemq::client::command;"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"        using namespace apache::activemq::client::io;"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"        using namespace apache::activemq::client::protocol;"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"/*"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|" *"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|" */"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"class "
operator|+
name|className
operator|+
literal|" : public "
operator|+
name|baseClass
operator|+
literal|""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"{"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"public:"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    "
operator|+
name|className
operator|+
literal|"() ;"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    virtual ~"
operator|+
name|className
operator|+
literal|"() ;"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    virtual IDataStructure* createCommand() ;"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    virtual char getDataStructureType() ;"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    "
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    virtual void unmarshal(ProtocolFormat& wireFormat, Object o, BinaryReader& dataIn, BooleanStream& bs) ;"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    virtual int marshal1(ProtocolFormat& wireFormat, Object& o, BooleanStream& bs) ;"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    virtual void marshal2(ProtocolFormat& wireFormat, Object& o, BinaryWriter& dataOut, BooleanStream& bs) ;"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"} ;"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"/* namespace */"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"     }"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    }"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"  }"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"#endif /*"
operator|+
name|className
operator|+
literal|"_hpp_*/"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|generateFactory
parameter_list|(
name|PrintWriter
name|out
parameter_list|)
block|{
name|generateLicence
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"// Marshalling code for Open Wire Format "
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"//"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"//"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"// NOTE!: This file is autogenerated - do not modify!"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"//        if you need to make a change, please see the Groovy scripts in the"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"//        activemq-openwire module"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"//"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"#ifndef MarshallerFactory_hpp_"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"#define MarshallerFactory_hpp_"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"namespace apache"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"{"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"  namespace activemq"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"  {"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    namespace client"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    {"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"      namespace marshal"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"      {"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"        using namespace ifr ;"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"        using namespace std ;"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"        using namespace apache::activemq::client;"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"        using namespace apache::activemq::client::command;"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"        using namespace apache::activemq::client::io;"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"/*"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|" * "
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|" */"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"class MarshallerFactory"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"{"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"public:"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    MarshallerFactory() ;"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    virtual ~MarshallerFactory() ;"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"	  virtual void configure(ProtocolFormat& format) ;"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"} ;"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"/* namespace */"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"      }"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    }"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"  }"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"#endif /*MarshallerFactory_hpp_*/"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|getTargetDir
parameter_list|()
block|{
return|return
name|targetDir
return|;
block|}
specifier|public
name|void
name|setTargetDir
parameter_list|(
name|String
name|targetDir
parameter_list|)
block|{
name|this
operator|.
name|targetDir
operator|=
name|targetDir
expr_stmt|;
block|}
block|}
end_class

end_unit


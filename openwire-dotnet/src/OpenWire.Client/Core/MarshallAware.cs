/*
 * Copyright 2006 The Apache Software Foundation or its licensors, as
 * applicable.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
using System;
using System.IO;

using OpenWire.Client.Commands;
using OpenWire.Client.Core;
using OpenWire.Client.IO;

namespace OpenWire.Client.Core {
        /// <summary>
        /// Represents a marshallable entity
        /// </summary>
        public interface MarshallAware {

                void BeforeMarshall(OpenWireFormat wireFormat);
                void AfterMarshall(OpenWireFormat wireFormat);

                void BeforeUnmarshall(OpenWireFormat wireFormat);
                void AfterUnmarshall(OpenWireFormat wireFormat);

                void SetMarshalledForm(OpenWireFormat wireFormat, byte[] data);
                byte[] GetMarshalledForm(OpenWireFormat wireFormat);
        }
}

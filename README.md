<img align="right" width="250" height="47" src="media/Gematik_Logo_Flag.png"/> <br/> 

# Keycloak-User-Purger

[![Quality Gate Status](https://sonar.prod.ccs.gematik.solutions/api/project_badges/measure?project=de.gematik.demis%3Akeycloak-user-purger&metric=alert_status&token=sqb_d80c2c5ac7194ad3596961f8fab983d374cdea12)](https://sonar.prod.ccs.gematik.solutions/dashboard?id=de.gematik.demis%3Akeycloak-user-purger)
[![Vulnerabilities](https://sonar.prod.ccs.gematik.solutions/api/project_badges/measure?project=de.gematik.demis%3Akeycloak-user-purger&metric=vulnerabilities&token=sqb_d80c2c5ac7194ad3596961f8fab983d374cdea12)](https://sonar.prod.ccs.gematik.solutions/dashboard?id=de.gematik.demis%3Akeycloak-user-purger)
[![Bugs](https://sonar.prod.ccs.gematik.solutions/api/project_badges/measure?project=de.gematik.demis%3Akeycloak-user-purger&metric=bugs&token=sqb_d80c2c5ac7194ad3596961f8fab983d374cdea12)](https://sonar.prod.ccs.gematik.solutions/dashboard?id=de.gematik.demis%3Akeycloak-user-purger)
[![Code Smells](https://sonar.prod.ccs.gematik.solutions/api/project_badges/measure?project=de.gematik.demis%3Akeycloak-user-purger&metric=code_smells&token=sqb_d80c2c5ac7194ad3596961f8fab983d374cdea12)](https://sonar.prod.ccs.gematik.solutions/dashboard?id=de.gematik.demis%3Akeycloak-user-purger)
[![Lines of Code](https://sonar.prod.ccs.gematik.solutions/api/project_badges/measure?project=de.gematik.demis%3Akeycloak-user-purger&metric=ncloc&token=sqb_d80c2c5ac7194ad3596961f8fab983d374cdea12)](https://sonar.prod.ccs.gematik.solutions/dashboard?id=de.gematik.demis%3Akeycloak-user-purger)
[![Coverage](https://sonar.prod.ccs.gematik.solutions/api/project_badges/measure?project=de.gematik.demis%3Akeycloak-user-purger&metric=coverage&token=sqb_d80c2c5ac7194ad3596961f8fab983d374cdea12)](https://sonar.prod.ccs.gematik.solutions/dashboard?id=de.gematik.demis%3Akeycloak-user-purger)


<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#release-notes">Release Notes</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#security-policy">Security Policy</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
  </ol>
</details>

## About The Project
This project checks all temporary users in the PORTAL realm and deletes them if the following properties are met:
- No login for more than 48 hours (configurable)
- Attribute 'accountIsTemporary' has the value 'true'

### Release Notes

See [ReleaseNotes.md](./ReleaseNotes.md) for all information regarding the (newest) releases.

## Getting Started

### Prerequisites

The Project requires Java 21 and Maven 3.8+.

### Installation

The Project can be built with the following command:

```sh
mvn clean install
```

The Docker Image associated to the service can be built with the extra profile `docker`:

```sh
mvn clean install -Pdocker
```

## Usage

The application can be executed from a JAR file or a Docker Image:

```sh
# As JAR Application
java -jar target/keycloak-user-purger.jar
# As Docker Image
docker run --rm -it -p 8080:8080 keycloak-user-purger:latest
```

It can also be deployed on Kubernetes by using the Helm Chart defined in the folder `deployment/helm/keycloak-user-purger`:

```ssh
helm install keycloak-user-purger ./deployment/helm/keycloak-user-purger
```

## Security Policy
If you want to see the security policy, please check our [SECURITY.md](.github/SECURITY.md).

## Contributing
If you want to contribute, please check our [CONTRIBUTING.md](.github/CONTRIBUTING.md).

## License
Copyright 2023-2025 gematik GmbH

EUROPEAN UNION PUBLIC LICENCE v. 1.2

EUPL © the European Union 2007, 2016

See the [LICENSE](./LICENSE.md) for the specific language governing permissions and limitations under the License

## Additional Notes and Disclaimer from gematik GmbH

1. Copyright notice: Each published work result is accompanied by an explicit statement of the license conditions for use. These are regularly typical conditions in connection with open source or free software. Programs described/provided/linked here are free software, unless otherwise stated.
2. Permission notice: Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
    1. The copyright notice (Item 1) and the permission notice (Item 2) shall be included in all copies or substantial portions of the Software.
    2. The software is provided "as is" without warranty of any kind, either express or implied, including, but not limited to, the warranties of fitness for a particular purpose, merchantability, and/or non-infringement. The authors or copyright holders shall not be liable in any manner whatsoever for any damages or other claims arising from, out of or in connection with the software or the use or other dealings with the software, whether in an action of contract, tort, or otherwise.
    3. We take open source license compliance very seriously. We are always striving to achieve compliance at all times and to improve our processes. If you find any issues or have any suggestions or comments, or if you see any other ways in which we can improve, please reach out to: ospo@gematik.de
3. Please note: Parts of this code may have been generated using AI-supported technology. Please take this into account, especially when troubleshooting, for security analyses and possible adjustments.

## Contact
E-Mail to [DEMIS Entwicklung](mailto:demis-entwicklung@gematik.de?subject=[GitHub]%20Keycloak-User-Purger)